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
  const sportlerName = url.searchParams.get("sportlerName") ?? "";
  const kategorie = url.searchParams.get("kategorie") ?? "";

  if (!jwt_token) throw redirect(303, "/login");
  if (!userIsAdmin(locals.user)) throw redirect(303, "/");

  const fokusParams = new URLSearchParams({ page, size });
  if (sportlerName) fokusParams.set("sportlerName", sportlerName);
  if (kategorie) fokusParams.set("kategorie", kategorie);

  try {
    const [fokusResponse, sportlerResponse] = await Promise.all([
      axios.get(`${API_BASE_URL}/api/trainingsfokus?${fokusParams}`, {
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
      sportler: sportlerResponse.data.content ?? sportlerResponse.data,
      sportlerName,
      kategorie
    };
  } catch (err) {
    if (err.response?.status === 403) throw redirect(303, "/");
    return {
      trainingsfokus: [],
      pagination: { page, size, totalElements: 0, totalPages: 0 },
      sportler: [],
      sportlerName,
      kategorie
    };
  }
}

export const actions = {
  createTrainingsfokus: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;
    if (!jwt_token) throw error(401, "Authentication required");
    if (!userIsAdmin(locals.user)) throw redirect(303, "/");

    const data = await request.formData();
    const schwerpunktRaw = data.get("schwerpunkt");
    const schwerpunkt = schwerpunktRaw === "Andere"
      ? (data.get("schwerpunktAndere") ?? "Andere").slice(0, 50)
      : schwerpunktRaw;

    const parseIntOrNull = (v) => { const n = parseInt(v); return isNaN(n) ? null : n; };

    const turnierdatum = data.get("turnierdatum") || null;
    let dauerWochen = null;
    if (turnierdatum) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const diff = new Date(turnierdatum) - today;
      if (diff > 0) dauerWochen = Math.ceil(diff / (7 * 24 * 60 * 60 * 1000));
    }

    const fokus = {
      kategorie: data.get("kategorie"),
      schwerpunkt,
      notiz: (data.get("notiz") ?? "").slice(0, 300),
      beschreibung: "",
      status: "AKTIV",
      sportlerId: data.get("sportlerId"),
      turniername: data.get("turniername") || null,
      turnierdatum,
      dauerWochen,
      einheitenProWoche: parseIntOrNull(data.get("einheitenProWoche")),
      minutenProEinheit: parseIntOrNull(data.get("minutenProEinheit"))
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

  updateStatus: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;
    if (!jwt_token) throw error(401, "Authentication required");
    if (!userIsAdmin(locals.user)) throw redirect(303, "/");

    const data = await request.formData();
    const id = data.get("id");
    const status = data.get("status");

    try {
      await axios({
        method: "put",
        url: `${API_BASE_URL}/api/trainingsfokus/${id}/status?status=${status}`,
        headers: { Authorization: "Bearer " + jwt_token }
      });
      return { success: true };
    } catch {
      return { success: false, error: "Status konnte nicht geändert werden." };
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
