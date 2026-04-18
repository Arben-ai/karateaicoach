import axios from "axios";
import { redirect, error } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;
const DEFAULT_PAGE_SIZE = 5;
const SPORTLER_SELECT_PAGE_SIZE = 100;

function userIsAdmin(user) {
  return Array.isArray(user?.user_roles)
    && user.user_roles.some((role) => typeof role === "string" && role.toLowerCase() === "admin");
}

export async function load({ locals, url }) {
  const jwt_token = locals.jwt_token;
  const pageParam = Number.parseInt(url.searchParams.get("page") ?? "0", 10);
  const sizeParam = Number.parseInt(url.searchParams.get("size") ?? `${DEFAULT_PAGE_SIZE}`, 10);
  const page = Number.isNaN(pageParam) || pageParam < 0 ? 0 : pageParam;
  const size = Number.isNaN(sizeParam) || sizeParam < 1 ? DEFAULT_PAGE_SIZE : sizeParam;

  if (!jwt_token) throw redirect(303, "/login");
  if (!userIsAdmin(locals.user)) throw redirect(303, "/");

  try {
    const [fokusResponse, sportlerResponse] = await Promise.all([
      axios.get(`${API_BASE_URL}/api/trainingsfokus?page=${page}&size=${size}`, {
        headers: { Authorization: "Bearer " + jwt_token }
      }),
      axios.get(`${API_BASE_URL}/api/sportler?page=0&size=${SPORTLER_SELECT_PAGE_SIZE}`, {
        headers: { Authorization: "Bearer " + jwt_token }
      })
    ]);

    return {
      trainingsfokus: fokusResponse.data.content ?? fokusResponse.data,
      pagination: {
        page: fokusResponse.data.page ?? page,
        size: fokusResponse.data.size ?? size,
        totalElements: fokusResponse.data.totalElements ?? (fokusResponse.data.content?.length ?? 0),
        totalPages: fokusResponse.data.totalPages ?? 1
      },
      sportler: sportlerResponse.data.content ?? sportlerResponse.data
    };
  } catch (err) {
    if (err.response?.status === 403) throw redirect(303, "/");
    return {
      trainingsfokus: [],
      pagination: { page, size, totalElements: 0, totalPages: 0 },
      sportler: []
    };
  }
}

export const actions = {
  createTrainingsfokus: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;
    if (!jwt_token) throw error(401, "Authentication required");
    if (!userIsAdmin(locals.user)) throw redirect(303, "/");

    const data = await request.formData();
    const fokus = {
      beschreibung: data.get("beschreibung"),
      schwerpunkt: data.get("schwerpunkt"),
      status: data.get("status"),
      sportlerId: data.get("sportlerId")
    };

    try {
      await axios({
        method: "post",
        url: `${API_BASE_URL}/api/trainingsfokus`,
        headers: { "Content-Type": "application/json", Authorization: "Bearer " + jwt_token },
        data: fokus
      });
      return { success: true };
    } catch (err) {
      return {
        success: false,
        error: err.response?.status === 403
          ? "Nur Admins dürfen Trainingsfokusse erstellen."
          : "Trainingsfokus konnte nicht erstellt werden."
      };
    }
  },

  deleteTrainingsfokus: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;
    if (!jwt_token) throw error(401, "Authentication required");
    if (!userIsAdmin(locals.user)) throw redirect(303, "/");

    const data = await request.formData();
    const id = data.get("id");

    try {
      await axios({
        method: "delete",
        url: `${API_BASE_URL}/api/trainingsfokus/${id}`,
        headers: { Authorization: "Bearer " + jwt_token }
      });
      return { success: true };
    } catch (err) {
      return { success: false, error: "Trainingsfokus konnte nicht gelöscht werden." };
    }
  }
};
