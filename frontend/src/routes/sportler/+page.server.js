import axios from "axios";
import { redirect, error } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;
const DEFAULT_PAGE_SIZE = 8;

function userIsAdmin(user) {
  return Array.isArray(user?.user_roles)
    && user.user_roles.some((role) => typeof role === "string" && role.toLowerCase() === "admin");
}

export async function load({ locals, url }) {
  const jwt_token = locals.jwt_token;
  const isAdmin = userIsAdmin(locals.user);

  if (!jwt_token) {
    throw redirect(303, "/login");
  }

  // Admin sieht alle Sportler (paginiert)
  if (isAdmin) {
    const pageParam = Number.parseInt(url.searchParams.get("page") ?? "0", 10);
    const sizeParam = Number.parseInt(url.searchParams.get("size") ?? `${DEFAULT_PAGE_SIZE}`, 10);
    const page = Number.isNaN(pageParam) || pageParam < 0 ? 0 : pageParam;
    const size = Number.isNaN(sizeParam) || sizeParam < 1 ? DEFAULT_PAGE_SIZE : sizeParam;
    const search = url.searchParams.get("search") ?? "";
    const guertelgrad = url.searchParams.get("guertelgrad") ?? "";

    const params = new URLSearchParams({ page, size });
    if (search) params.set("search", search);
    if (guertelgrad) params.set("guertelgrad", guertelgrad);

    try {
      const response = await axios.get(`${API_BASE_URL}/api/sportler?${params}`, {
        headers: { Authorization: "Bearer " + jwt_token }
      });

      return {
        isAdmin: true,
        sportler: response.data.content ?? response.data,
        pagination: {
          page: response.data.page ?? page,
          size: response.data.size ?? size,
          totalElements: response.data.totalElements ?? (response.data.content?.length ?? 0),
          totalPages: response.data.totalPages ?? 1
        },
        search,
        guertelgrad,
        mySportler: null
      };
    } catch (err) {
      return {
        isAdmin: true,
        sportler: [],
        pagination: { page: 0, size: DEFAULT_PAGE_SIZE, totalElements: 0, totalPages: 0 },
        search,
        guertelgrad,
        mySportler: null
      };
    }
  }

  // Sportler sieht nur sein eigenes Profil
  try {
    const response = await axios.get(`${API_BASE_URL}/api/sportler/me`, {
      headers: { Authorization: "Bearer " + jwt_token }
    });
    return { isAdmin: false, mySportler: response.data, sportler: [], pagination: null };
  } catch (err) {
    if (err.response?.status === 404) {
      return { isAdmin: false, mySportler: null, sportler: [], pagination: null };
    }
    return { isAdmin: false, mySportler: null, sportler: [], pagination: null };
  }
}

export const actions = {
  createSportler: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;
    if (!jwt_token) throw error(401, "Authentication required");

    const data = await request.formData();
    const gewicht = parseFloat(data.get("gewicht"));
    const sportler = {
      name: data.get("name"),
      email: data.get("email"),
      guertelgrad: data.get("guertelgrad") || "",
      gewicht: isNaN(gewicht) ? 0 : gewicht
    };

    try {
      await axios({
        method: "post",
        url: `${API_BASE_URL}/api/sportler`,
        headers: { "Content-Type": "application/json", Authorization: "Bearer " + jwt_token },
        data: sportler
      });
      return { success: true };
    } catch (err) {
      if (err.response?.status === 409) {
        return { success: false, error: "Ein Sportler mit diesem Namen oder dieser E-Mail existiert bereits." };
      }
      return { success: false, error: "Profil konnte nicht erstellt werden." };
    }
  },

  deleteSportler: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;
    if (!jwt_token) throw error(401, "Authentication required");

    const data = await request.formData();
    const id = data.get("id");

    try {
      await axios({
        method: "delete",
        url: `${API_BASE_URL}/api/sportler/${id}`,
        headers: { Authorization: "Bearer " + jwt_token }
      });
      return { success: true, deleted: true, auth0Warning: true };
    } catch (err) {
      return {
        success: false,
        error: err.response?.status === 403
          ? "Du kannst nur dein eigenes Profil löschen."
          : "Profil konnte nicht gelöscht werden."
      };
    }
  }
};
