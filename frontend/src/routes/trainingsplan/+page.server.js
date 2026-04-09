import axios from "axios";
import { redirect } from "@sveltejs/kit";

const API_BASE_URL = process.env.API_BASE_URL;

export async function load({ locals }) {
  const jwt_token = locals.jwt_token;

  // ❗ Kein Login → redirect
  if (!jwt_token) {
    throw redirect(303, "/login");
  }

  try {
    const response = await axios({
      method: "get",
      url: `${API_BASE_URL}/api/trainingsplan`,
      headers: {
        Authorization: "Bearer " + jwt_token
      }
    });

    return {
      trainingsplan: response.data
    };

  } catch (err) {
    console.log("Error loading trainingsplan:", err);
    return {
      trainingsplan: []
    };
  }
}