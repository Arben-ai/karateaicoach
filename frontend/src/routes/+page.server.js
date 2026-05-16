import axios from 'axios';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL;

function userIsAdmin(user) {
  return Array.isArray(user?.user_roles) &&
    user.user_roles.some((r) => r.toLowerCase() === 'admin');
}

export async function load({ locals }) {
  const { user, isAuthenticated, jwt_token } = locals;
  let sportler = null;
  let profileIncomplete = false;

  let unreadCount = 0;

  if (isAuthenticated && !userIsAdmin(user) && jwt_token) {
    try {
      const [sportlerRes, feedbackRes] = await Promise.all([
        axios.get(`${API_BASE_URL}/api/sportler/me`, {
          headers: { Authorization: 'Bearer ' + jwt_token }
        }),
        axios.get(`${API_BASE_URL}/api/trainingsfokus/me?size=50`, {
          headers: { Authorization: 'Bearer ' + jwt_token }
        })
      ]);
      sportler = sportlerRes.data;
      profileIncomplete = !sportler.guertelgrad || !sportler.gewicht;
      const feedbacks = feedbackRes.data?.content ?? feedbackRes.data ?? [];
      unreadCount = feedbacks.filter((f) => !f.gelesen).length;
    } catch {}
  }

  return { profileIncomplete, sportler, unreadCount };
}

export const actions = {
  completeProfile: async ({ request, locals }) => {
    const { jwt_token } = locals;
    if (!jwt_token) return { success: false, error: 'Nicht authentifiziert.' };

    const data = await request.formData();
    const guertelgrad = data.get('guertelgrad') ?? '';
    const gewichtRaw = parseFloat(data.get('gewicht'));
    const gewicht = isNaN(gewichtRaw) ? 0 : gewichtRaw;

    if (!guertelgrad) return { success: false, error: 'Bitte wähle deinen Gürtelgrad.' };
    if (gewicht <= 0) return { success: false, error: 'Bitte gib ein gültiges Gewicht ein.' };

    try {
      await axios.patch(`${API_BASE_URL}/api/sportler/me`, { guertelgrad, gewicht }, {
        headers: { Authorization: 'Bearer ' + jwt_token }
      });
      return { success: true };
    } catch {
      return { success: false, error: 'Profil konnte nicht gespeichert werden.' };
    }
  }
};
