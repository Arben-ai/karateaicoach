import { redirect } from '@sveltejs/kit';
import auth from '$lib/server/auth.service.js';
import axios from 'axios';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL;

export const actions = {
  signup: async ({ request, cookies }) => {
    const data = await request.formData();
    const email = data.get('email');
    const password = data.get('password');
    const firstName = data.get('firstName');
    const lastName = data.get('lastName');
    try {
      const validationResponse = await axios({
        method: 'get',
        url: `https://disify.com/api/email/${email}`
      });

      if (!validationResponse.data.format || validationResponse.data.disposable || !validationResponse.data.dns) {
        return { error: `Die E-Mail-Adresse ${email} ist ungültig oder gehört zu einer temporären Domain.` };
      }

      await auth.signup(email, password, firstName, lastName, cookies);

      const jwt_token = cookies.get('jwt_token');
      if (jwt_token) {
        const name = [firstName, lastName].filter(Boolean).join(' ') || email;
        await axios.post(`${API_BASE_URL}/api/sportler/me`, { name, email }, {
          headers: { Authorization: 'Bearer ' + jwt_token }
        }).catch((err) => console.error('Sportler create error:', err.response?.data ?? err.message));
      }
    } catch (error) {
      const code = error.response?.data?.code ?? error.response?.data?.name;
      if (code === 'user_exists' || error.response?.status === 400) {
        return { error: 'Diese E-Mail-Adresse ist bereits registriert. Bitte melde dich stattdessen an.' };
      }
      console.error('Signup error:', error);
      return { error: 'Registrierung fehlgeschlagen. Bitte versuche es erneut.' };
    }

    throw redirect(303, '/');
  }
};
