import { Platform } from 'react-native';

function getDefaultBaseUrl() {
  // Android emulator: specjalny alias do hosta (czyli Twojego Windowsa), gdzie działa Docker/port 8080.
  if (Platform.OS === 'android') return 'http://10.0.2.2:8080';

  // Web: zwykły localhost.
  if (Platform.OS === 'web') return 'http://localhost:8080';

  // iOS simulator zwykle widzi localhost hosta; na fizycznym urządzeniu to będzie "localhost telefonu",
  // więc w praktyce warto nadpisać to przez EXPO_PUBLIC_API_BASE_URL.
  return 'http://localhost:8080';
}

export const API_BASE_URL = process.env.EXPO_PUBLIC_API_BASE_URL ?? getDefaultBaseUrl();

// Backend exposes: GET /public/demo-data
export const DEMO_DATA_ENDPOINT = `${API_BASE_URL}/public/demo-data`;
