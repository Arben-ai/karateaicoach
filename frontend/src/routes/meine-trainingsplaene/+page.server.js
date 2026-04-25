import axios from "axios";
import { redirect } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;

export async function load({ locals, url }) {
  const jwt_token = locals.jwt_token;
  if (!jwt_token) throw redirect(303, "/login");

  const isAdmin = Array.isArray(locals.user?.user_roles) &&
    locals.user.user_roles.some((r) => r.toLowerCase() === "admin");
  if (isAdmin) throw redirect(303, "/trainingsplan");

  const highlightPlanId = url.searchParams.get("planId") ?? null;
  const page = Number.parseInt(url.searchParams.get("page") ?? "0", 10);
  const size = 5;

  try {
    const response = await axios.get(
      `${API_BASE_URL}/api/trainingsplan/me?page=${page}&size=${size}`,
      { headers: { Authorization: "Bearer " + jwt_token } }
    );

    return {
      trainingsplaene: response.data.content ?? [],
      pagination: {
        page: response.data.page ?? page,
        size: response.data.size ?? size,
        totalElements: response.data.totalElements ?? 0,
        totalPages: response.data.totalPages ?? 1
      },
      highlightPlanId
    };
  } catch {
    return {
      trainingsplaene: [],
      pagination: { page, size, totalElements: 0, totalPages: 1 },
      highlightPlanId
    };
  }
}
