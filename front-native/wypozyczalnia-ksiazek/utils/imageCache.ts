import { Platform } from 'react-native';
import * as FileSystem from 'expo-file-system';

const isNative = Platform.OS === 'ios' || Platform.OS === 'android';

const nativeCacheBase = isNative
  ? ('cacheDirectory' in (FileSystem as any)
      ? ((FileSystem as any).cacheDirectory as string | null | undefined)
      : undefined) ?? (FileSystem as any).Paths?.cache?.uri
  : undefined;

const DIR = isNative && nativeCacheBase ? `${nativeCacheBase}demo-images/` : '';

function safeFileNameFromUrl(url: string): string {
  const base = encodeURIComponent(url);
  return `${base}.img`;
}

async function ensureDir() {
  if (!isNative) return;
  const info = await FileSystem.getInfoAsync(DIR);
  if (!info.exists) {
    await FileSystem.makeDirectoryAsync(DIR, { intermediates: true });
  }
}

export async function getOrDownloadImage(url: string): Promise<string> {
  if (!url || !url.startsWith('http')) {
    return url;
  }

  if (!isNative) {
    return url;
  }

  await ensureDir();
  const fileUri = `${DIR}${safeFileNameFromUrl(url)}`;

  const existing = await FileSystem.getInfoAsync(fileUri);
  if (existing.exists && existing.size && existing.size > 0) {
    return fileUri;
  }

  const result = await FileSystem.downloadAsync(url, fileUri);
  return result.uri;
}

export function collectImageUrls(value: any, acc: Set<string> = new Set()): Set<string> {
  if (value === null || value === undefined) return acc;

  if (Array.isArray(value)) {
    for (const item of value) collectImageUrls(item, acc);
    return acc;
  }

  if (typeof value === 'object') {
    for (const [k, v] of Object.entries(value)) {
      if (k === 'imageUrl' && typeof v === 'string' && v.startsWith('http')) {
        acc.add(v);
      } else {
        collectImageUrls(v, acc);
      }
    }
    return acc;
  }

  return acc;
}

export function replaceImageUrlsWithLocal(value: any, map: Record<string, string>): any {
  if (value === null || value === undefined) return value;

  if (Array.isArray(value)) {
    return value.map((v) => replaceImageUrlsWithLocal(v, map));
  }

  if (typeof value === 'object') {
    const out: any = Array.isArray(value) ? [] : { ...value };
    for (const [k, v] of Object.entries(value)) {
      if (k === 'imageUrl' && typeof v === 'string' && map[v]) {
        out[k] = map[v];
      } else {
        out[k] = replaceImageUrlsWithLocal(v, map);
      }
    }
    return out;
  }

  return value;
}
