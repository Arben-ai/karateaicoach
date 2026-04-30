import axios from "axios";
import { redirect, error, fail } from "@sveltejs/kit";
import { env } from "$env/dynamic/private";

const API_BASE_URL = env.API_BASE_URL;

export async function load({ locals, url }) {
  if (!locals.jwt_token) {
    throw redirect(303, "/login");
  }

  const mode = url.searchParams.get("mode") ?? null;
  const fokusId = url.searchParams.get("fokusId") ?? null;
  const schwerpunkt = url.searchParams.get("schwerpunkt") ?? null;
  const kategorie = url.searchParams.get("kategorie") ?? null;

  let initialMessage = url.searchParams.get("message") ?? null;

  if (mode === "plan" && schwerpunkt) {
    const kat = kategorie ? ` (${kategorie})` : "";
    initialMessage = `Zeige mir bitte, wie ein Trainingsplan für den Schwerpunkt "${schwerpunkt}"${kat} aussehen würde.`;
  }

  return { initialMessage, mode, fokusId, schwerpunkt, kategorie };
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
      const status = err.response?.status;
      const detail = err.response?.data || err.message;
      console.error("Chat error:", status, detail);
      return {
        success: false,
        error: `Fehler ${status ?? ""}: ${detail ?? "Antwort konnte nicht geladen werden."}`
      };
    }
  },

  savePlan: async ({ request, locals }) => {
    const jwt_token = locals.jwt_token;
    if (!jwt_token) return fail(401, { error: "Nicht authentifiziert." });

    const data = await request.formData();
    const fokusId = data.get("fokusId");
    if (!fokusId) return fail(400, { error: "Fokus-ID fehlt." });

    try {
      const res = await axios.post(
        `${API_BASE_URL}/api/trainingsplan/aus-fokus/${fokusId}`,
        {},
        {
          headers: { Authorization: "Bearer " + jwt_token },
          timeout: 90000
        }
      );
      const plan = res.data;
      return { planSaved: true, planId: plan.id, planTitel: plan.titel, planText: plan.inhalt ?? plan.beschreibung ?? null };
    } catch (err) {
      return fail(500, { error: "Trainingsplan konnte nicht gespeichert werden." });
    }
  }
};
