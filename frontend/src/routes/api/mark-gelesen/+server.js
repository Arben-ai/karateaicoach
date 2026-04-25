import axios from 'axios';
import { env } from '$env/dynamic/private';
import { json } from '@sveltejs/kit';

const API_BASE_URL = env.API_BASE_URL;

export async function POST({ request, locals }) {
  const { jwt_token } = locals;
  if (!jwt_token) return json({ success: false }, { status: 401 });

  const { id } = await request.json();
  if (!id) return json({ success: false }, { status: 400 });

  try {
    await axios.patch(`${API_BASE_URL}/api/trainingsfokus/${id}/gelesen`, {}, {
      headers: { Authorization: 'Bearer ' + jwt_token }
    });
    return json({ success: true });
  } catch {
    return json({ success: false }, { status: 500 });
  }
}
