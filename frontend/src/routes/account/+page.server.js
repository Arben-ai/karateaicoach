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
