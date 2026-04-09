import axios from "axios";
import { redirect, error } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;

export async function load({ locals }) {
  const jwt_token = locals.jwt_token;

  if (!jwt_token) {
    throw redirect(303, "/login");
  }

  try {
    const response = await axios({
      method: "get",
      url: `${API_BASE_URL}/api/sportler`,
      headers: {
        Authorization: "Bearer " + jwt_token
      }
    });

    return {
      sportler: response.data
    };

  } catch (err) {
    console.log("Error loading sportler:", err);
    return {
      sportler: []
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