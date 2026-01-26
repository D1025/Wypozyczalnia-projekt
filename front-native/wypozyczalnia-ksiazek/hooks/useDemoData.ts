import { useCallback, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import NetInfo from '@react-native-community/netinfo';

import { DEMO_DATA_ENDPOINT } from '@/constants/api';
import { collectImageUrls, getOrDownloadImage, replaceImageUrlsWithLocal } from '@/utils/imageCache';

const CACHE_KEY = 'demo-data-cache-v3';

export interface BookDimensions {
  widthMm: number;
  heightMm: number;
  thicknessMm: number;
}

export interface BookSpecs {
  coverType: string;
  pages: number;
  language: string;
  dimensions: BookDimensions;
}

export interface DemoBook {
  id: string;
  title: string;
  author: string;
  isbn: string;
  publishedYear: number;
  genre: string;
  description: string;
  totalCopies: number;
  availableCopies: number;
  imageUrl: string;
  specs: BookSpecs;
}

export interface DemoData {
  timestamp: string;
  books: DemoBook[];
}

type CacheShape = {
  savedAt: string;
  data: DemoData;
  imageMap?: Record<string, string>;
};

async function readCache(): Promise<CacheShape | null> {
  const raw = await AsyncStorage.getItem(CACHE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as CacheShape;
  } catch {
    return null;
  }
}

async function writeCache(payload: CacheShape) {
  await AsyncStorage.setItem(CACHE_KEY, JSON.stringify(payload));
}

async function downloadImagesForOffline(data: DemoData): Promise<Record<string, string>> {
  const urls = Array.from(collectImageUrls(data));
  const map: Record<string, string> = {};

  for (const url of urls) {
    try {
      map[url] = await getOrDownloadImage(url);
    } catch {
    }
  }

  return map;
}

export function useDemoData() {
  const [data, setData] = useState<DemoData | null>(null);
  const [savedAt, setSavedAt] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isRefreshing, setIsRefreshing] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [isOnline, setIsOnline] = useState<boolean | null>(null);

  useEffect(() => {
    const sub = NetInfo.addEventListener((state) => {
      setIsOnline(Boolean(state.isConnected && state.isInternetReachable !== false));
    });
    return () => sub();
  }, []);

  const loadFromCache = useCallback(async () => {
    const cached = await readCache();
    if (cached?.data) {
      const hydrated = cached.imageMap ? replaceImageUrlsWithLocal(cached.data, cached.imageMap) : cached.data;
      setData(hydrated);
      setSavedAt(cached.savedAt ?? null);
      return true;
    }
    return false;
  }, []);

  const fetchFromApi = useCallback(async () => {
    const res = await fetch(DEMO_DATA_ENDPOINT, {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`);
    }

    const json = (await res.json()) as DemoData;

    const imageMap = await downloadImagesForOffline(json);
    const hydrated = replaceImageUrlsWithLocal(json, imageMap);

    setData(hydrated);
    setSavedAt(new Date().toISOString());

    await writeCache({
      savedAt: new Date().toISOString(),
      data: json,
      imageMap,
    });
  }, []);

  const load = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      await loadFromCache();

      const state = await NetInfo.fetch();
      const onlineNow = Boolean(state.isConnected && state.isInternetReachable !== false);
      if (onlineNow) {
        await fetchFromApi();
      }
    } catch (e: any) {
      const hadCache = await loadFromCache();
      if (!hadCache) {
        setError(e?.message ?? 'Load failed');
      }
    } finally {
      setIsLoading(false);
    }
  }, [fetchFromApi, loadFromCache]);

  const refresh = useCallback(async () => {
    setIsRefreshing(true);
    setError(null);
    try {
      const state = await NetInfo.fetch();
      const onlineNow = Boolean(state.isConnected && state.isInternetReachable !== false);
      if (!onlineNow) {
        throw new Error('Offline');
      }
      await fetchFromApi();
    } catch (e: any) {
      setError(e?.message ?? 'Refresh failed');
    } finally {
      setIsRefreshing(false);
    }
  }, [fetchFromApi]);

  useEffect(() => {
    void load();
  }, [load]);

  return {
    data,
    savedAt,
    isLoading,
    isRefreshing,
    isOnline,
    error,
    refresh,
  };
}
