import axios from "axios";
import { redirect } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;

export async function load({ locals }) {
  const jwt_token = locals.jwt_token;

  console.log("LOAD RUNNING");

  if (!jwt_token) {
    throw redirect(303, "/login");
  }

  const trainingsplanResponse = await axios.get(
    `${API_BASE_URL}/api/trainingsplan`,
    {
      headers: {
        Authorization: "Bearer " + jwt_token
      }
    }
  );

  const sportlerResponse = await axios.get(
    `${API_BASE_URL}/api/sportler`,
    {
      headers: {
        Authorization: "Bearer " + jwt_token
      }
    }
  );

  return {
    trainingsplan: trainingsplanResponse.data,
    sportler: sportlerResponse.data
  };
}