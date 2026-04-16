import axios from "axios";
import { redirect, error } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;
const DEFAULT_PAGE_SIZE = 5;
const SPORTLER_SELECT_PAGE_SIZE = 100;

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
    const trainingsplanResponse = await axios.get(
      `${API_BASE_URL}/api/trainingsplan?page=${page}&size=${size}`,
      {
        headers: {
          Authorization: "Bearer " + jwt_token
        }
      }
    );

    const sportlerResponse = await axios.get(
      `${API_BASE_URL}/api/sportler?page=0&size=${SPORTLER_SELECT_PAGE_SIZE}`,
      {
        headers: {
          Authorization: "Bearer " + jwt_token
        }
      }
    );

    return {
      trainingsplan: trainingsplanResponse.data.content ?? trainingsplanResponse.data,
      trainingsplanPagination: {
        page: trainingsplanResponse.data.page ?? page,
        size: trainingsplanResponse.data.size ?? size,
        totalElements: trainingsplanResponse.data.totalElements
          ?? (trainingsplanResponse.data.content?.length ?? trainingsplanResponse.data.length ?? 0),
        totalPages: trainingsplanResponse.data.totalPages ?? 1
      },
      sportler: sportlerResponse.data.content ?? sportlerResponse.data
    };
  } catch (err) {
    if (err.response?.status === 403) {
      throw redirect(303, "/");
    }

    console.log("Error loading trainingsplan:", err);
    return {
      trainingsplan: [],
      trainingsplanPagination: {
        page,
        size,
        totalElements: 0,
        totalPages: 0
      },
      sportler: []
    };
  }
}

export const actions = {
  createTrainingsplan: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;

    if (!jwt_token) {
      throw error(401, "Authentication required");
    }

    const data = await request.formData();
    const trainingsplan = {
      titel: data.get("titel"),
      dauer: Number(data.get("dauer")),
      status: data.get("status"),
      sportlerId: data.get("sportlerId")
    };

    try {
      await axios({
        method: "post",
        url: `${API_BASE_URL}/api/trainingsplan`,
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + jwt_token
        },
        data: trainingsplan
      });

      return { success: true };
    } catch (err) {
      console.log("Error creating trainingsplan:", err);
      return {
        success: false,
        error: err.response?.status === 403
          ? "Nur Admins duerfen Trainingsplaene erstellen."
          : "Trainingsplan konnte nicht erstellt werden."
      };
    }
  }
};
