import axios from "axios";
import { error } from '@sveltejs/kit';
import 'dotenv/config';

const API_BASE_URL = process.env.API_BASE_URL;

export async function load() {

    try {
        const trainingsplanResponse = await axios({
            method: "get",
            url: `${API_BASE_URL}/api/trainingsplan`,
        });

        const sportlerResponse = await axios({
            method: "get",
            url: `${API_BASE_URL}/api/sportler`,
        });

        return {
            trainingsplaene: trainingsplanResponse.data,
            sportler: sportlerResponse.data
        };

    } catch (axiosError) {
        console.log('Error loading data:', axiosError);
        throw error(500, 'Error loading data');
    }
}

export const actions = {
    createTrainingsplan: async ({ request }) => {

        const data = await request.formData();

        const trainingsplan = {
            titel: data.get('titel'),
            dauer: parseInt(data.get('dauer')),
            status: data.get('status'),
            sportlerId: data.get('sportlerId')
        };

        try {
            await axios({
                method: "post",
                url: `${API_BASE_URL}/api/trainingsplan`,
                headers: {
                    "Content-Type": "application/json",
                },
                data: trainingsplan,
            });

            return { success: true };

        } catch (err) {
            console.log('Error creating trainingsplan:', err);
            return { success: false, error: 'Could not create trainingsplan' };
        }
    }
};