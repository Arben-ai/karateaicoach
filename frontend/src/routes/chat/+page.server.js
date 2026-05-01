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

  if (mode === "plan" && fokusId) {
    const jwt = locals.jwt_token;
    const headers = { Authorization: "Bearer " + jwt };

    const [fokusRes, sportlerRes] = await Promise.allSettled([
      axios.get(`${API_BASE_URL}/api/trainingsfokus/me?size=50`, { headers }),
      axios.get(`${API_BASE_URL}/api/sportler/me`, { headers })
    ]);

    const fokusse = fokusRes.status === "fulfilled" ? (fokusRes.value.data.content ?? []) : [];
    const fokus = fokusse.find(f => f.id === fokusId);
    const sportler = sportlerRes.status === "fulfilled" ? sportlerRes.value.data : null;

    const sw = fokus?.schwerpunkt ?? schwerpunkt ?? "unbekannt";
    const kat = fokus?.kategorie ?? kategorie ?? null;
    const notiz = fokus?.notiz ?? null;
    const guertelgrad = sportler?.guertelgrad || null;
    const gewicht = sportler?.gewicht ? `${sportler.gewicht} kg` : null;
    const dauerWochen = fokus?.dauerWochen ?? null;
    const einheitenProWoche = fokus?.einheitenProWoche ?? null;
    const minutenProEinheit = fokus?.minutenProEinheit ?? null;
    const turniername = fokus?.turniername ?? null;
    const turnierdatum = fokus?.turnierdatum ?? null;

    const turnierdatumFormatiert = turnierdatum
      ? new Date(turnierdatum).toLocaleDateString("de-CH", { day: "2-digit", month: "2-digit", year: "numeric" })
      : null;

    const lines = [
      `Ich bin Mitglied des Nationalkaders und bereite mich auf einen Wettkampf vor. Erstelle mir bitte einen detaillierten, wettkampforientierten Trainingsplan:`,
      ``,
      turniername && turnierdatumFormatiert
        ? `- Zielturnier: ${turniername} am ${turnierdatumFormatiert}${dauerWochen ? ` (in ${dauerWochen} Woche${dauerWochen > 1 ? "n" : ""})` : ""}`
        : null,
      `- Trainingsschwerpunkt für diesen Plan: ${sw}`,
      kat ? `- Kategorie: ${kat}` : null,
      guertelgrad ? `- Mein Gürtelgrad: ${guertelgrad}` : null,
      gewicht ? `- Mein Gewicht: ${gewicht}` : null,
      einheitenProWoche ? `- Trainingseinheiten pro Woche: ${einheitenProWoche}×` : null,
      minutenProEinheit ? `- Dauer pro Einheit: ${minutenProEinheit} Minuten` : null,
      notiz ? `- Spezifische Anweisung vom Coach: "${notiz}"` : null,
      ``,
      `Bitte strukturiere jede Einheit mit Aufwärmen, Hauptteil und Cooldown. Passe Intensität und Volumen dem Gürtelgrad und${dauerWochen ? ` dem verbleibenden Zeitfenster von ${dauerWochen} Woche${dauerWochen > 1 ? "n" : ""} bis zum Turnier` : " dem Wettkampfziel"} an.`
    ].filter(l => l !== null);

    initialMessage = lines.join("\n");
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
