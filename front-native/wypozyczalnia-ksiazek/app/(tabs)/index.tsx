import {
  StyleSheet,
  View,
  useWindowDimensions,
  LayoutAnimation,
  Pressable,
  Platform,
  UIManager,
} from 'react-native';
import { Image } from 'expo-image';
import { useState } from 'react';

import ParallaxScrollView from '@/components/parallax-scroll-view';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { useDemoData, DemoBook } from '@/hooks/useDemoData';
import { useColorScheme } from '@/hooks/use-color-scheme'; // Add this import
import React from 'react';
import { IconSymbol } from '@/components/ui/icon-symbol'; // Add this if you want to use IconSymbol, or use FontAwesome as before.

if (Platform.OS === 'android' && UIManager.setLayoutAnimationEnabledExperimental) {
  UIManager.setLayoutAnimationEnabledExperimental(true);
}

function BookCard({ book, width, onPress, isSelected }: { book: DemoBook; width: number; onPress: (b: DemoBook) => void; isSelected?: boolean }) {
  const colorScheme = useColorScheme();
  const activeBorder = colorScheme === 'dark' ? 'rgba(255,255,255,0.5)' : 'rgba(0,0,0,0.4)';

  return (
    <Pressable
      onPress={() => onPress(book)}
      style={[
        styles.bookCard,
        { width },
        isSelected && { borderColor: activeBorder, borderWidth: 2 }
      ]}
    >
      <Image source={{ uri: book.imageUrl }} style={styles.bookCover} contentFit="cover" />
      <View style={styles.bookInfo}>
        <ThemedText numberOfLines={2} style={styles.bookTitle}>
          {book.title}
        </ThemedText>
        <ThemedText numberOfLines={1} style={styles.bookAuthor}>
          {book.author}
        </ThemedText>
        <ThemedText numberOfLines={1} style={styles.detailText}>
          {book.genre} • {book.publishedYear}
        </ThemedText>
      </View>
    </Pressable>
  );
}

function ExpandedBook({ book, expandedMore, onToggleMore, onClose }: { book: DemoBook; expandedMore: boolean; onToggleMore: () => void; onClose: () => void }) {
  const colorScheme = useColorScheme();
  const borderColor = colorScheme === 'dark' ? 'rgba(255,255,255,0.1)' : 'rgba(0,0,0,0.06)';

  return (
    <View style={styles.expandedWrapper}>
      <ThemedView style={[styles.overlayCard, { borderColor }]}>
        <View style={{ flexDirection: 'row', gap: 12 }}>
          <Image source={{ uri: book.imageUrl }} style={styles.overlayImage} contentFit="cover" />
          <View style={{ flex: 1, flexShrink: 1 }}>
            <ThemedText type="defaultSemiBold" style={styles.overlayTitle} numberOfLines={2}>{book.title}</ThemedText>
            <ThemedText style={styles.overlaySubtitle} numberOfLines={1}>{book.author} • {book.publishedYear}</ThemedText>

            <View style={{ marginTop: 8 }}>
              <ThemedText style={{ fontSize: 13, opacity: 0.9 }} numberOfLines={expandedMore ? undefined : 3}>
                {book.description}
              </ThemedText>

              {book.specs && (
                <View style={{ marginTop: 8 }}>
                  <ThemedText style={styles.detailText}>Oprawa: {book.specs.coverType}</ThemedText>
                  <ThemedText style={styles.detailText}>Stron: {book.specs.pages}</ThemedText>
                  {book.specs.dimensions && (
                    <ThemedText style={styles.detailText}>
                      Wymiary: {book.specs.dimensions.widthMm}x{book.specs.dimensions.heightMm}x{book.specs.dimensions.thicknessMm} mm
                    </ThemedText>
                  )}
                </View>
              )}
            </View>

            <View style={{ flexDirection: 'row', gap: 8, marginTop: 10, flexWrap: 'wrap' }}>
              <Pressable style={styles.actionBtn} onPress={onToggleMore}>
                <ThemedText style={styles.actionText}>{expandedMore ? 'Pokaż mniej' : 'Pokaż więcej'}</ThemedText>
              </Pressable>
              <Pressable style={styles.actionBtn} onPress={onClose}>
                <ThemedText style={styles.actionText}>Zamknij</ThemedText>
              </Pressable>
            </View>

          </View>
        </View>
      </ThemedView>
    </View>
  );
}

function BooksGrid({ data, onBookPress, selectedId, onToggleMore, expandedMore }: { data: DemoBook[]; onBookPress: (b: DemoBook) => void; selectedId: string | null; onToggleMore: () => void; expandedMore: boolean }) {
  const { width } = useWindowDimensions();
  const padding = 24;
  const gap = 12;
  const numColumns = width > 900 ? 5 : width > 600 ? 4 : 2;

  // Calculate available width logic:
  // On Web, the vertical scrollbar takes up space (~17px on Windows) which is included in window width
  // but reduces the viewport width available for views. We subtract a buffer to prevent overflow.
  const scrollbarBuffer = Platform.OS === 'web' ? 18 : 0;

  // Total Width - Side Paddings - Total Gaps between columns - Scrollbar Buffer
  const availableWidth = width - (padding * 2) - (gap * (numColumns - 1)) - scrollbarBuffer;
  const cardWidth = Math.floor(availableWidth / numColumns);

  // Split into rows logically (chunking) to preserve grid structure
  const rows: DemoBook[][] = [];
  for (let i = 0; i < data.length; i += numColumns) {
    rows.push(data.slice(i, i + numColumns));
  }

  return (
    <View style={styles.sectionContainer}>
        {rows.map((row, rowIndex) => {
          const selectedBook = row.find(b => b.id === selectedId);

          return (
            <View key={rowIndex}>
              {/* The Grid Row */}
              <View style={[styles.gridRow, { gap, paddingHorizontal: 0, marginBottom: selectedBook ? 0 : gap }]}>
                {row.map((book) => (
                  <BookCard
                    key={book.id}
                    book={book}
                    width={cardWidth}
                    onPress={onBookPress}
                    isSelected={book.id === selectedId}
                  />
                ))}
              </View>

              {/* Expanded details panel inserted right after the row containing selected item */}
              {selectedBook && (
                 <View style={{ marginTop: gap, marginBottom: gap, paddingHorizontal: 0, width: '100%' }}>
                   <ExpandedBook
                     book={selectedBook}
                     expandedMore={expandedMore}
                     onToggleMore={onToggleMore}
                     onClose={() => onBookPress(selectedBook)}
                   />
                 </View>
              )}
            </View>
          );
        })}
    </View>
  );
}

export default function HomeScreen() {
  const { data, error, isRefreshing, isOnline, savedAt, refresh } = useDemoData();
  const [selected, setSelected] = useState<DemoBook | null>(null);
  const [expandedMore, setExpandedMore] = useState(false);

  const onBookPress = (b: DemoBook) => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setSelected(prev => (prev && prev.id === b.id ? null : b));
    if (selected && selected.id !== b.id) setExpandedMore(false); // Reset more when changing selection
    else if (selected && selected.id === b.id) setExpandedMore(false); // Reset on close
  };

  const onToggleMore = () => {
    LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
    setExpandedMore(v => !v);
  };

  return (
    <ParallaxScrollView
      headerBackgroundColor={{ light: '#A1CEDC', dark: '#1D3D47' }}
      headerImage={
        // Placeholder for a nicer header or removed entirely if only title is needed
        <View style={styles.headerSpacer} />
      }>
      <ThemedView style={styles.header}>
        <View style={styles.titleRow}>
           <ThemedText type="title">Wypożyczalnia książek</ThemedText>
           {/* Modern Refresh Button */}
           <Pressable
              onPress={() => refresh()}
              style={({ pressed }) => [
                styles.iconBtn,
                pressed && { opacity: 0.6, transform: [{ scale: 0.96 }] },
                isRefreshing && { opacity: 0.5 }
              ]}
              hitSlop={10}
              disabled={isRefreshing}
            >
              <IconSymbol name="arrow.clockwise" size={20} color="#0a7ea4" />
           </Pressable>
        </View>

        <ThemedText style={styles.meta}>
          Status: {isOnline === null ? '...' : isOnline ? 'online' : 'offline'}
          {savedAt ? ` • ${new Date(savedAt).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}` : ''}
        </ThemedText>

        {error ? <ThemedText style={styles.error}>Błąd: {error}</ThemedText> : null}
      </ThemedView>

      {/* Books Grid - Main Content */}
      {data?.books && (
        <BooksGrid
          data={data.books}
          onBookPress={(b) => onBookPress(b)}
          selectedId={selected?.id ?? null}
          onToggleMore={onToggleMore}
          expandedMore={expandedMore}
        />
      )}

      <View style={{ height: 40 }} />

    </ParallaxScrollView>
  );
}

const styles = StyleSheet.create({
  header: {
    gap: 8,
    marginBottom: 20,
    paddingHorizontal: 24, // aligned with grid padding
  },
  headerSpacer: {
     height: 100, // Reduced height since we removed the big logo image
  },
  titleRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 4,
  },
  iconBtn: {
    padding: 8,
    borderRadius: 20,
    backgroundColor: 'rgba(150,150,150,0.1)',
  },
  meta: {
    opacity: 0.7,
    fontSize: 12
  },
  error: {
    color: '#d33',
    marginTop: 8
  },
  actions: {
    marginTop: 6,
    flexDirection: 'row',
  },
  refreshBtn: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: 'rgba(150,150,150,0.5)',
    overflow: 'hidden',
    textAlign: 'center'
  },
  sectionContainer: {
      marginBottom: 24,
  },
  gridRow: {
    flexDirection: 'row',
    // flexWrap is not needed here as we chunk manually, but safe to keep default or 'nowrap'
  },
  grid: {
      // flexDirection: 'row', // Removed old grid style
      // flexWrap: 'wrap',
  },
  bookCard: {
      backgroundColor: 'rgba(150,150,150,0.08)',
      borderRadius: 12,
      overflow: 'hidden',
      borderWidth: 1,
      borderColor: 'rgba(150,150,150,0.15)',
      // marginBottom removed, handled by row gap
  },
  bookCover: {
      width: '100%',
      aspectRatio: 2 / 3,
      backgroundColor: '#eaeaea'
  },
  bookInfo: {
      padding: 10,
      gap: 4
  },
  bookTitle: {
      fontSize: 14,
      fontWeight: '600',
      lineHeight: 18
  },
  bookAuthor: {
      fontSize: 12,
      opacity: 0.7
  },
  bookDetails: {
      marginTop: 8,
      paddingTop: 8,
      borderTopWidth: StyleSheet.hairlineWidth,
      borderTopColor: 'rgba(150,150,150,0.3)',
      gap: 2
  },
  detailText: {
      fontSize: 11,
      opacity: 0.8
  },
  // expanded inline
  expandedWrapper: {
    marginBottom: 8,
    width: '100%', // ensure full width
  },
  overlayCard: {
    borderRadius: 12,
    // backgroundColor handled by ThemedView
    borderWidth: 1,
    // borderColor handled dynamic
    padding: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.08,
    shadowRadius: 8,
    elevation: 6,
  },
  overlayImage: {
    width: 100,
    maxWidth: '30%', // Responsive constraint for small screens
    height: 140,
    borderRadius: 8,
    backgroundColor: '#ddd',
  },
  overlayTitle: { fontSize: 16, flexShrink: 1 }, // Ensure title shrinks
  overlaySubtitle: { fontSize: 12, opacity: 0.6 },
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
