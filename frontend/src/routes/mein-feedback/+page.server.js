import axios from 'axios';
import { redirect } from '@sveltejs/kit';
import { env } from '$env/dynamic/private';

const API_BASE_URL = env.API_BASE_URL;

export async function load({ locals }) {
  const jwt_token = locals.jwt_token;
  if (!jwt_token) throw redirect(303, '/login');

  const isAdmin = Array.isArray(locals.user?.user_roles) &&
    locals.user.user_roles.some((r) => r.toLowerCase() === 'admin');
  if (isAdmin) throw redirect(303, '/trainingsfokus');

  const [fokusRes, planRes] = await Promise.allSettled([
    axios.get(`${API_BASE_URL}/api/trainingsfokus/me?size=50`, {
      headers: { Authorization: 'Bearer ' + jwt_token }
    }),
    axios.get(`${API_BASE_URL}/api/trainingsplan/me?size=100`, {
      headers: { Authorization: 'Bearer ' + jwt_token }
    })
  ]);

  const trainingsfokusse = fokusRes.status === 'fulfilled'
    ? (fokusRes.value.data.content ?? [])
    : [];

  const planByFokusId = {};
  if (planRes.status === 'fulfilled') {
    for (const plan of (planRes.value.data.content ?? [])) {
      if (plan.trainingsfokusId) {
        planByFokusId[plan.trainingsfokusId] = plan;
      }
    }
  }

  return { trainingsfokusse, planByFokusId };
}
