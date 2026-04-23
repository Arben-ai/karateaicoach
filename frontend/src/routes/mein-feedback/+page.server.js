import axios from "axios";
import { redirect } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;

export async function load({ locals }) {
  const jwt_token = locals.jwt_token;
  if (!jwt_token) throw redirect(303, "/login");

  const isAdmin = Array.isArray(locals.user?.user_roles) &&
    locals.user.user_roles.some((r) => r.toLowerCase() === "admin");
  if (isAdmin) throw redirect(303, "/trainingsfokus");

  try {
    const response = await axios.get(`${API_BASE_URL}/api/trainingsfokus/me?size=50`, {
      headers: { Authorization: "Bearer " + jwt_token }
    });

    return {
      trainingsfokusse: response.data.content ?? [],
      pagination: {
        totalElements: response.data.totalElements ?? 0
      }
    };
  } catch {
    return { trainingsfokusse: [], pagination: { totalElements: 0 } };
  }
}
