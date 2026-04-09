import axios from "axios";
import { error } from '@sveltejs/kit';
import 'dotenv/config';

const API_BASE_URL = process.env.API_BASE_URL;

export async function load() {

    try {
        const response = await axios({
            method: "get",
            url: `${API_BASE_URL}/api/sportler`,
        });

        return {
            sportler: response.data
        };

    } catch (axiosError) {
        console.log('Error loading sportler:', axiosError);
        throw error(500, 'Error loading sportler');
    }
}

export const actions = {
    createSportler: async ({ request }) => {

        const data = await request.formData();

        const sportler = {
            name: data.get('name'),
            email: data.get('email')
        };

        try {
            await axios({
                method: "post",
                url: `${API_BASE_URL}/api/sportler`,
                headers: {
                    "Content-Type": "application/json",
                },
                data: sportler,
            });

            return { success: true };

        } catch (err) {
            console.log('Error creating sportler:', err);
            return { success: false, error: 'Could not create sportler' };
        }
    }
};