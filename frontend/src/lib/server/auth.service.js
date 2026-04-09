import axios from "axios";
import 'dotenv/config';

const AUTH0_DOMAIN = process.env.AUTH0_DOMAIN;
const AUTH0_CLIENT_ID = process.env.AUTH0_CLIENT_ID;

// SIGNUP
async function signup(
  email,
  password,
  firstName = null,
  lastName = null,
  cookies
) {
  const options = {
    method: "post",
    url: `https://${AUTH0_DOMAIN}/dbconnections/signup`,
    data: {
      client_id: AUTH0_CLIENT_ID,
      email: email,
      password: password,
      connection: "Username-Password-Authentication",
    },
  };

  if (firstName && firstName.length > 0) {
    options.data.given_name = firstName;
  }

  if (lastName && lastName.length > 0) {
    options.data.family_name = lastName;
  }

  try {
    await axios(options);

    // kleine Verzögerung (Auth0 Rollen etc.)
    await new Promise(resolve => setTimeout(resolve, 2000));

    return await login(email, password, cookies);
  } catch (error) {
    throw error;
  }
}

// LOGIN
async function login(username, password, cookies) {
  const options = {
    method: "post",
    url: `https://${AUTH0_DOMAIN}/oauth/token`,
    data: {
      grant_type: "password",
      username: username,
      password: password,
      audience: "karate-api",
      scope: "openid profile email",
      client_id: AUTH0_CLIENT_ID,
    },
  };

  try {
    const response = await axios(options);
    const { access_token } = response.data;

    console.log("ACCESS TOKEN:", access_token);

    // User Info holen
    const userInfo = await getUserInfo(access_token);

    // ✅ WICHTIG: access_token speichern (NICHT id_token!)
    cookies.set('jwt_token', access_token, {
      path: '/',
      maxAge: 60 * 60 * 24 * 7,
      sameSite: 'lax',
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production'
    });

    cookies.set('user_info', JSON.stringify(userInfo), {
      path: '/',
      maxAge: 60 * 60 * 24 * 7,
      sameSite: 'lax',
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production'
    });

    return { success: true };
  } catch (error) {
    throw error;
  }
}

// USER INFO
async function getUserInfo(access_token) {
  const options = {
    method: "get",
    url: `https://${AUTH0_DOMAIN}/oauth/userinfo`,
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + access_token,
    },
  };

  try {
    const response = await axios(options);
    return response.data;
  } catch (error) {
    throw error;
  }
}

const auth = {
  signup,
  login,
};

export default auth;