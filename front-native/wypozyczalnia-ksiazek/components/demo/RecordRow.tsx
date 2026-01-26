import { useMemo, useState, useRef, useEffect } from 'react';
import { Pressable, StyleSheet, View, LayoutAnimation, Platform, Animated, Dimensions } from 'react-native';
import { Image } from 'expo-image';
import FontAwesome from '@expo/vector-icons/FontAwesome';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { useColorScheme } from '@/hooks/use-color-scheme.web';

function tryGetImageUrl(obj: any): string | null {
  if (!obj) return null;
  if (typeof obj === 'object') {
    const v = obj.imageUrl;
    if (typeof v === 'string') return v;
  }
  return null;
}

export function RecordRow({ title, value }: { title: string; value: any }) {
  const [isOpen, setIsOpen] = useState(false);
  const [showFullJson, setShowFullJson] = useState(false);
  const [expandedMore, setExpandedMore] = useState(false);
  const colorScheme = useColorScheme();
  const iconColor = colorScheme === 'dark' ? '#C0C0C0' : '#606060';

  const imageUrl = useMemo(() => {
    if (Array.isArray(value) && value.length > 0) {
      return tryGetImageUrl(value[0]);
    }
    return tryGetImageUrl(value);
  }, [value]);

  const summary = useMemo(() => {
    if (Array.isArray(value)) return `${value.length} items`;
    if (typeof value === 'object' && value !== null) {
        const keys = Object.keys(value);
        return keys.length > 0 ? `{ ${keys.slice(0, 3).join(', ')}${keys.length > 3 ? '...' : ''} }` : '{}';
    }
    return String(value);
  }, [value]);

  // Animated values for overlay
  const overlayAnim = useRef(new Animated.Value(0)).current; // 0 = closed, 1 = open
  const moreAnim = useRef(new Animated.Value(0)).current; // 0 = collapsed, 1 = more

  useEffect(() => {
    if (Platform.OS !== 'web') {
      LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    }
    Animated.timing(overlayAnim, {
      toValue: isOpen ? 1 : 0,
      duration: 260,
      useNativeDriver: true,
    }).start();

    Animated.timing(moreAnim, {
      toValue: expandedMore ? 1 : 0,
      duration: 220,
      useNativeDriver: false,
    }).start();
  }, [isOpen, expandedMore]);

  const toggleOpen = () => {
    setIsOpen(v => !v);
    if (isOpen) {
      setShowFullJson(false);
      setExpandedMore(false);
    }
  };

  const screenWidth = Dimensions.get('window').width;

  return (
    <View style={styles.wrapper}>
      <ThemedView style={styles.card}>
        <Pressable onPress={toggleOpen} style={styles.header}>
          <View style={styles.headerContent}>
            {imageUrl && (
              <Image source={{ uri: imageUrl }} style={styles.thumb} contentFit="cover" />
            )}
            <View style={{ flex: 1, justifyContent: 'center' }}>
              <ThemedText type="defaultSemiBold" style={styles.title}>{title}</ThemedText>
              <ThemedText style={styles.summary} numberOfLines={1}>{summary}</ThemedText>
            </View>
             <FontAwesome
                name={isOpen ? 'chevron-up' : 'chevron-down'}
                size={14}
                color={iconColor}
                style={{ marginLeft: 8 }}
              />
          </View>
        </Pressable>

        {isOpen && (
          <View style={styles.content}>
               <View style={styles.previewContainer}>
                  {typeof value === 'object' && value !== null && !Array.isArray(value) && (
                      Object.entries(value).slice(0, 5).map(([k, v]) => (
                          <View key={k} style={styles.propRow}>
                              <ThemedText style={styles.propKey}>{k}:</ThemedText>
                              <ThemedText style={styles.propValue} numberOfLines={1}>
                                  {typeof v === 'object' ? '[Object]' : String(v)}
                              </ThemedText>
                          </View>
                      ))
                  )}
                   {Array.isArray(value) && (
                      <ThemedText style={styles.propValue}>Array with {value.length} elements.</ThemedText>
                   )}
              </View>

            {!showFullJson ? (
               <Pressable style={styles.showMoreBtn} onPress={() => setShowFullJson(true)}>
                  <ThemedText style={styles.showMoreText}>Pokaż więcej (JSON)</ThemedText>
               </Pressable>
            ) : (
              <View style={styles.jsonBox}>
                <ThemedText style={styles.jsonText}>
                   {JSON.stringify(value, null, 2)}
                </ThemedText>
                <Pressable style={styles.showMoreBtn} onPress={() => setShowFullJson(false)}>
                  <ThemedText style={styles.showMoreText}>Zwiń</ThemedText>
               </Pressable>
              </View>
            )}

            <Pressable style={styles.moreToggle} onPress={() => setExpandedMore(v => !v)}>
              <ThemedText style={styles.moreToggleText}>{expandedMore ? 'Pokaż mniej' : 'Pokaż więcej'}</ThemedText>
            </Pressable>
          </View>
        )}
      </ThemedView>

      {isOpen && (
        <Animated.View
          pointerEvents="box-none"
          style={[
            styles.overlay,
            {
              transform: [
                {
                  translateY: overlayAnim.interpolate({
                    inputRange: [0, 1],
                    outputRange: [-8, 0],
                  }),
                },
              ],
              opacity: overlayAnim,
              width: screenWidth - 32,
            },
          ]}
        >
          <ThemedView style={styles.overlayCard}>
            <View style={styles.overlayInner}>
              {imageUrl && (
                <Image source={{ uri: imageUrl }} style={styles.overlayImage} contentFit="cover" />
              )}
              <View style={{ flex: 1, paddingLeft: 12 }}>
                <ThemedText type="defaultSemiBold" style={styles.overlayTitle}>{title}</ThemedText>
                <ThemedText style={styles.overlaySubtitle}>{summary}</ThemedText>

                {/* More expanded area, animated height */}
                <Animated.View style={{
                  height: moreAnim.interpolate({ inputRange: [0, 1], outputRange: [0, 180] }),
                  overflow: 'hidden',
                  marginTop: 8,
                }}>
                  <ThemedText style={{ fontSize: 12, opacity: 0.85 }} numberOfLines={10}>
                    {typeof value === 'object' ? JSON.stringify(value, null, 2) : String(value)}
                  </ThemedText>
                </Animated.View>

                <View style={{ flexDirection: 'row', gap: 8, marginTop: 8 }}>
                  <Pressable style={styles.actionBtn} onPress={() => {/* placeholder action */}}>
                    <ThemedText style={styles.actionText}>Akcje</ThemedText>
                  </Pressable>
                  <Pressable style={styles.actionBtn} onPress={() => { setIsOpen(false); setExpandedMore(false); }}>
                    <ThemedText style={styles.actionText}>Zamknij</ThemedText>
                  </Pressable>
                </View>
              </View>
            </View>
          </ThemedView>
        </Animated.View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    position: 'relative',
    marginBottom: 12,
  },
  card: {
    borderRadius: 12,
    overflow: 'hidden',
    backgroundColor: 'rgba(150,150,150,0.08)',
    borderWidth: 1,
    borderColor: 'rgba(150,150,150,0.15)',
  },
  header: {
    padding: 12,
  },
  headerContent: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  thumb: {
    width: 56,
    height: 56,
    borderRadius: 8,
    backgroundColor: '#ccc',
  },
  title: {
    fontSize: 16,
  },
  summary: {
    fontSize: 12,
    opacity: 0.6,
  },
  content: {
    paddingHorizontal: 12,
    paddingBottom: 12,
    borderTopWidth: 1,
    borderTopColor: 'rgba(150,150,150,0.1)',
  },
  previewContainer: {
      paddingVertical: 8,
      gap: 4
  },
  propRow: {
      flexDirection: 'row',
      gap: 8,
      alignItems: 'center'
  },
  propKey: {
      fontSize: 12,
      opacity: 0.7,
      fontFamily: 'SpaceMono', // or bold
      minWidth: 80
  },
  propValue: {
      fontSize: 12,
      flex: 1
  },
  showMoreBtn: {
    marginTop: 8,
    alignSelf: 'flex-start',
    backgroundColor: 'rgba(0,122,255,0.1)',
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 8,
  },
  showMoreText: {
    fontSize: 12,
    color: '#0a7ea4',
    fontWeight: '600',
  },
  jsonBox: {
    marginTop: 8,
    backgroundColor: 'rgba(0,0,0,0.03)',
    borderRadius: 8,
    padding: 8,
  },
  jsonText: {
    fontFamily: 'Courier', // fallback or monospace
    fontSize: 10,
    opacity: 0.8,
  },
  moreToggle: {
    marginTop: 8,
  },
  moreToggleText: {
    fontSize: 13,
    color: '#0a7ea4',
    fontWeight: '600',
  },
  overlay: {
    position: 'absolute',
    left: 16,
    top: 8,
    zIndex: 1000,
  },
  overlayCard: {
    borderRadius: 12,
    backgroundColor: 'white',
    borderWidth: 1,
    borderColor: 'rgba(0,0,0,0.06)',
    padding: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.08,
    shadowRadius: 8,
    elevation: 6,
  },
  overlayInner: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  overlayImage: {
    width: 100,
    height: 140,
    borderRadius: 8,
    backgroundColor: '#ddd',
  },
  overlayTitle: {
    fontSize: 16,
  },
  overlaySubtitle: {
    fontSize: 12,
    opacity: 0.6,
  },
  actionBtn: {
    backgroundColor: 'rgba(0,122,255,0.06)',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 8,
  },
  actionText: {
    color: '#0a7ea4',
    fontWeight: '600'
  }
});
