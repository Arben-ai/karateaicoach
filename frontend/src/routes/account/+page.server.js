import axios from 'axios';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL;

function userIsAdmin(user) {
    return Array.isArray(user?.user_roles)
        && user.user_roles.some((r) => typeof r === 'string' && r.toLowerCase() === 'admin');
}

export async function load({ locals }) {
    const { user, isAuthenticated, jwt_token } = locals;
    let sportler = null;

    if (isAuthenticated && !userIsAdmin(user) && jwt_token) {
        try {
            const res = await axios.get(`${API_BASE_URL}/api/sportler/me`, {
                headers: { Authorization: 'Bearer ' + jwt_token }
            });
            sportler = res.data;
        } catch {}
    }

    return { user, isAuthenticated, sportler };
}

export const actions = {
    updateProfile: async ({ request, locals }) => {
        const { jwt_token } = locals;
        if (!jwt_token) return { success: false, error: 'Nicht authentifiziert.' };

        const data = await request.formData();
        const guertelgrad = data.get('guertelgrad') ?? '';
        const gewichtRaw = parseFloat(data.get('gewicht'));
        const gewicht = isNaN(gewichtRaw) ? 0 : gewichtRaw;

        try {
            await axios.patch(`${API_BASE_URL}/api/sportler/me`, { guertelgrad, gewicht }, {
                headers: { Authorization: 'Bearer ' + jwt_token }
            });
            return { success: true };
        } catch {
            return { success: false, error: 'Profil konnte nicht aktualisiert werden.' };
        }
    }
};
