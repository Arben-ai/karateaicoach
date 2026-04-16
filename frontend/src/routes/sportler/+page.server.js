import axios from "axios";
import { redirect, error } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;
const DEFAULT_PAGE_SIZE = 5;

export async function load({ locals, url }) {
  const jwt_token = locals.jwt_token;
  const pageParam = Number.parseInt(url.searchParams.get("page") ?? "0", 10);
  const sizeParam = Number.parseInt(url.searchParams.get("size") ?? `${DEFAULT_PAGE_SIZE}`, 10);
  const page = Number.isNaN(pageParam) || pageParam < 0 ? 0 : pageParam;
  const size = Number.isNaN(sizeParam) || sizeParam < 1 ? DEFAULT_PAGE_SIZE : sizeParam;

  if (!jwt_token) {
    throw redirect(303, "/login");
  }

  try {
    const response = await axios({
      method: "get",
      url: `${API_BASE_URL}/api/sportler?page=${page}&size=${size}`,
      headers: {
        Authorization: "Bearer " + jwt_token
      }
    });

    return {
      sportler: response.data.content ?? response.data,
      pagination: {
        page: response.data.page ?? page,
        size: response.data.size ?? size,
        totalElements: response.data.totalElements ?? (response.data.content?.length ?? response.data.length ?? 0),
        totalPages: response.data.totalPages ?? 1
      }
    };
  } catch (err) {
    console.log("Error loading sportler:", err);
    return {
      sportler: [],
      pagination: {
        page,
        size,
        totalElements: 0,
        totalPages: 0
      }
    };
  }
}

export const actions = {
  createSportler: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;

    if (!jwt_token) {
      throw error(401, "Authentication required");
    }

    const data = await request.formData();

    const sportler = {
      name: data.get("name"),
      email: data.get("email")
    };

    try {
      await axios({
        method: "post",
        url: `${API_BASE_URL}/api/sportler`,
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + jwt_token
        },
        data: sportler
      });

      return { success: true };
    } catch (err) {
      console.log("Error creating sportler:", err);
      return { success: false };
    }
  }
};
