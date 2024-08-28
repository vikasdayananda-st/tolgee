import { useMutation } from 'react-query';
import axios from 'axios';
// Define the API endpoint
const API_URL =
  'http://localhost:8083/https://api.stayfreeapps.com/v1/ai/query';

// Function to perform the translation
export const translateStrings = async ({
  prompt,
  model = 'gpt-4o',
  temperature = 0.7,
  team = 'staywise',
}) => {
  // Get API key from environment variables
  const apiKey = import.meta.env.VITE_APP_API_KEY;

  // Prepare the request payload
  const payload = {
    prompt,
    model,
    temperature,
    team,
  };

  // Send POST request to the API with the Authorization header
  const response = await axios.post(API_URL, payload, {
    headers: {
      'API-KEY': apiKey,
      'Content-Type': 'application/json',
    },
  });
  const responseData = response.data.response;

  // Assuming the API response contains a JSON-formatted string
  return responseData
};

// React Query hook for translation
const useTranslate = () => {
  return useMutation(translateStrings);
};

export default useTranslate;
