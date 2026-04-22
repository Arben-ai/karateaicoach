import axios from "axios";
import { redirect, error } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;

export async function load({ locals }) {
  if (!locals.jwt_token) {
    throw redirect(303, "/login");
  }
  return {};
}

export const actions = {
  send: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;

    if (!jwt_token) {
      throw error(401, "Authentication required");
    }

    const data = await request.formData();
    const message = data.get("message")?.toString().trim();

    if (!message) {
      return { success: false, error: "Nachricht darf nicht leer sein." };
    }

    try {
      const response = await axios({
        method: "post",
        url: `${API_BASE_URL}/api/chat`,
        headers: {
          "Content-Type": "text/plain",
          Authorization: "Bearer " + jwt_token
        },
        data: message
      });

      return { success: true, reply: response.data };
    } catch (err) {
      console.error("Chat error:", err);
      return {
        success: false,
        error: "Antwort konnte nicht geladen werden."
      };
    }
  }
};
