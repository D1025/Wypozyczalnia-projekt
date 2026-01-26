import { Platform } from 'react-native';

function getDefaultBaseUrl() {
  if (Platform.OS === 'android') return 'http://192.168.0.101:8080';

  if (Platform.OS === 'web') return 'http://localhost:8080';

  return 'http://192.168.0.101:8080';
}

export const API_BASE_URL = process.env.EXPO_PUBLIC_API_BASE_URL ?? getDefaultBaseUrl();

export const DEMO_DATA_ENDPOINT = `${API_BASE_URL}/public/demo-data`;
