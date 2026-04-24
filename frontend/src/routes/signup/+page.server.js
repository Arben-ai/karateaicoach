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
    const guertelgrad = data.get('guertelgrad') || '';
    const gewicht = parseFloat(data.get('gewicht')) || 0;

    try {
      await auth.signup(email, password, firstName, lastName, cookies);

      const jwt_token = cookies.get('jwt_token');
      if (jwt_token) {
        const name = [firstName, lastName].filter(Boolean).join(' ') || email;
        await axios.post(`${API_BASE_URL}/api/sportler/me`, { name, email, guertelgrad, gewicht }, {
          headers: { Authorization: 'Bearer ' + jwt_token }
        }).catch((err) => console.error('Sportler create error:', err.response?.data ?? err.message));
      }
    } catch (error) {
      console.error('Signup error:', error);
      return { error: 'Signup failed. Please try again.' };
    }

    throw redirect(303, '/');
  }
};
