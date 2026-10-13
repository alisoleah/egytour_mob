# EgyTour Design System

## Design Philosophy

**Aesthetic Direction**: Ancient Egyptian Elegance meets Modern Digital Wayfinding

EgyTour's design language draws inspiration from:
- The timeless beauty of hieroglyphics and Egyptian art
- The warm, earthy tones of the desert and Nile
- The clarity of museum wayfinding systems
- The immersion of premium audio guide devices

**Core Principle**: "History in Your Hands" - The UI should feel like wielding a magical artifact that reveals hidden stories of ancient Egypt.

---

## Color Palette

### Primary Colors

```css
:root {
  /* ═══════════════════════════════════════════════════════════════
     PRIMARY: Pharaoh's Gold - Heritage, Wisdom, Egyptian Identity
     ═══════════════════════════════════════════════════════════════ */
  --color-primary-50:  #FFF9E6;
  --color-primary-100: #FFEEB3;
  --color-primary-200: #FFE380;
  --color-primary-300: #FFD84D;
  --color-primary-400: #FFCD1A;
  --color-primary-500: #E6A800;  /* Main Brand Color - Egyptian Gold */
  --color-primary-600: #CC9500;
  --color-primary-700: #B38200;
  --color-primary-800: #996F00;
  --color-primary-900: #805C00;
  
  /* ═══════════════════════════════════════════════════════════════
     SECONDARY: Nile Blue - Trust, Navigation, Water of Life
     ═══════════════════════════════════════════════════════════════ */
  --color-nile-50:  #E6F4F9;
  --color-nile-100: #B3DFED;
  --color-nile-200: #80CAE1;
  --color-nile-300: #4DB5D5;
  --color-nile-400: #26A5CC;
  --color-nile-500: #0095C4;  /* Navigation, Maps, Information */
  --color-nile-600: #007EA6;
  --color-nile-700: #006788;
  --color-nile-800: #00506A;
  --color-nile-900: #00394C;
  
  /* ═══════════════════════════════════════════════════════════════
     ACCENT: Papyrus Orange - Energy, Discovery, Sunrise
     ═══════════════════════════════════════════════════════════════ */
  --color-papyrus-50:  #FFF3E6;
  --color-papyrus-100: #FFDDB3;
  --color-papyrus-200: #FFC780;
  --color-papyrus-300: #FFB14D;
  --color-papyrus-400: #FF9F26;
  --color-papyrus-500: #E67E22;  /* CTAs, Highlights, Active States */
  --color-papyrus-600: #CC6B1A;
  --color-papyrus-700: #B35912;
  --color-papyrus-800: #99470A;
  --color-papyrus-900: #803502;
}
```

### Semantic Colors

```css
:root {
  /* Status Colors */
  --color-success:    #27AE60;  /* Downloaded, Fresh, Available */
  --color-warning:    #F39C12;  /* Stale data, Update available */
  --color-error:      #E74C3C;  /* Offline required, Closed site */
  --color-info:       #3498DB;  /* Tips, Historical notes */
  
  /* Audio State Indicators */
  --color-audio-playing:  #E67E22;  /* Currently playing */
  --color-audio-paused:   #7F8C8D;  /* Paused state */
  --color-audio-ready:    #27AE60;  /* Ready to play */
  
  /* AR Overlay Gradients */
  --gradient-ar-highlight: linear-gradient(135deg, #E6A800 0%, #E67E22 100%);
  --gradient-time-travel:  linear-gradient(180deg, rgba(230, 168, 0, 0.3) 0%, transparent 100%);
  
  /* Historical Period Colors */
  --color-period-pharaonic:  #E6A800;  /* Old/Middle/New Kingdom */
  --color-period-ptolemaic:  #0095C4;  /* Greek-Egyptian */
  --color-period-roman:      #9B59B6;  /* Roman Egypt */
  --color-period-islamic:    #27AE60;  /* Islamic era */
  --color-period-modern:     #34495E;  /* Modern Egypt */
}
```

### Neutral Palette (Dark Mode First)

```css
:root {
  /* Dark Theme (Default) - Desert Night */
  --color-bg-primary:    #0D0B09;   /* Deep sandstone black */
  --color-bg-secondary:  #1A1714;   /* Card backgrounds */
  --color-bg-tertiary:   #252220;   /* Elevated surfaces */
  --color-bg-hover:      #332F2B;   /* Interactive hover */
  
  --color-border:        #3D3832;   /* Subtle borders */
  --color-border-focus:  #E6A800;   /* Focus rings */
  
  --color-text-primary:  #FAFAF5;   /* Headlines - Warm white */
  --color-text-secondary:#B8B5AD;   /* Body text */
  --color-text-tertiary: #7A766D;   /* Captions, hints */
  --color-text-inverse:  #0D0B09;   /* Text on light bg */
}

/* Light Theme Override - Sunlit Papyrus */
[data-theme="light"] {
  --color-bg-primary:    #FAF8F5;   /* Papyrus white */
  --color-bg-secondary:  #FFFFFF;
  --color-bg-tertiary:   #F5F2ED;
  --color-bg-hover:      #EBE7E0;
  
  --color-border:        #E5E1D8;
  
  --color-text-primary:  #1A1714;
  --color-text-secondary:#5C574E;
  --color-text-tertiary: #8C867A;
}
```

### Site Category Colors

```css
:root {
  /* Site type identification */
  --color-site-pyramid:    #E6A800;  /* Pyramids, Tombs */
  --color-site-temple:     #0095C4;  /* Temples */
  --color-site-museum:     #9B59B6;  /* Museums */
  --color-site-market:     #27AE60;  /* Bazaars, Markets */
  --color-site-modern:     #E67E22;  /* Modern Cairo */
}
```

---

## Typography

### Font Stack

```css
:root {
  /* ═══════════════════════════════════════════════════════════════
     DISPLAY: Playfair Display - Elegant, Historical, Serif
     https://fonts.google.com/specimen/Playfair+Display
     ═══════════════════════════════════════════════════════════════ */
  --font-display: 'Playfair Display', 'Georgia', serif;
  
  /* ═══════════════════════════════════════════════════════════════
     BODY: Inter - Clean, Readable, Modern
     https://fonts.google.com/specimen/Inter
     ═══════════════════════════════════════════════════════════════ */
  --font-body: 'Inter', 'SF Pro Text', system-ui, sans-serif;
  
  /* ═══════════════════════════════════════════════════════════════
     ARABIC: Noto Naskh Arabic - Traditional, Readable
     https://fonts.google.com/noto/specimen/Noto+Naskh+Arabic
     ═══════════════════════════════════════════════════════════════ */
  --font-arabic: 'Noto Naskh Arabic', 'Inter', system-ui, sans-serif;
  
  /* ═══════════════════════════════════════════════════════════════
     HIEROGLYPHIC DISPLAY: Noto Sans Egyptian Hieroglyphs
     For decorative headings and authentic touches
     ═══════════════════════════════════════════════════════════════ */
  --font-hieroglyphic: 'Noto Sans Egyptian Hieroglyphs', system-ui;
  
  /* ═══════════════════════════════════════════════════════════════
     MONO: JetBrains Mono - Coordinates, Technical Data
     ═══════════════════════════════════════════════════════════════ */
  --font-mono: 'JetBrains Mono', 'SF Mono', monospace;
}
```

### Type Scale

```css
:root {
  /* Modular scale: 1.25 (Major Third) */
  --text-xs:   0.75rem;    /* 12px - Captions, timestamps */
  --text-sm:   0.875rem;   /* 14px - Secondary text */
  --text-base: 1rem;       /* 16px - Body text */
  --text-lg:   1.125rem;   /* 18px - Lead paragraphs */
  --text-xl:   1.25rem;    /* 20px - Card titles */
  --text-2xl:  1.5rem;     /* 24px - Section headers */
  --text-3xl:  1.875rem;   /* 30px - Page titles */
  --text-4xl:  2.25rem;    /* 36px - Site names */
  --text-5xl:  3rem;       /* 48px - Hero headlines */
  --text-6xl:  3.75rem;    /* 60px - Splash screens */
  
  /* Font weights */
  --font-regular:  400;
  --font-medium:   500;
  --font-semibold: 600;
  --font-bold:     700;
  
  /* Line heights */
  --leading-none:   1;
  --leading-tight:  1.25;
  --leading-snug:   1.375;
  --leading-normal: 1.5;
  --leading-relaxed: 1.75;  /* For longer historical text */
  
  /* Letter spacing */
  --tracking-tighter: -0.05em;
  --tracking-tight:   -0.025em;
  --tracking-normal:  0;
  --tracking-wide:    0.025em;
  --tracking-wider:   0.05em;   /* For hieroglyphic display */
}
```

### Typography Styles

```css
/* Display Headlines - Playfair Display */
.text-display-xl {
  font-family: var(--font-display);
  font-size: var(--text-5xl);
  font-weight: var(--font-bold);
  line-height: var(--leading-tight);
  letter-spacing: var(--tracking-tight);
}

/* Site Name */
.text-site-name {
  font-family: var(--font-display);
  font-size: var(--text-4xl);
  font-weight: var(--font-semibold);
  line-height: var(--leading-tight);
  color: var(--color-primary-500);
}

/* POI Title */
.text-poi-title {
  font-family: var(--font-body);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  line-height: var(--leading-snug);
}

/* Historical Period Label */
.text-period {
  font-family: var(--font-body);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  letter-spacing: var(--tracking-wider);
  text-transform: uppercase;
}

/* Body Text - Historical Content */
.text-body-historical {
  font-family: var(--font-body);
  font-size: var(--text-base);
  font-weight: var(--font-regular);
  line-height: var(--leading-relaxed);
  color: var(--color-text-secondary);
}

/* Audio Duration */
.text-duration {
  font-family: var(--font-mono);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  font-variant-numeric: tabular-nums;
}

/* Coordinates Display */
.text-coordinates {
  font-family: var(--font-mono);
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
}
```

---

## Spacing & Layout

```css
:root {
  /* Spacing scale (4px base) */
  --space-0:  0;
  --space-1:  0.25rem;   /* 4px */
  --space-2:  0.5rem;    /* 8px */
  --space-3:  0.75rem;   /* 12px */
  --space-4:  1rem;      /* 16px */
  --space-5:  1.25rem;   /* 20px */
  --space-6:  1.5rem;    /* 24px */
  --space-8:  2rem;      /* 32px */
  --space-10: 2.5rem;    /* 40px */
  --space-12: 3rem;      /* 48px */
  --space-16: 4rem;      /* 64px */
  --space-20: 5rem;      /* 80px */
  
  /* Border radius */
  --radius-sm:   0.375rem;  /* 6px - Badges */
  --radius-md:   0.5rem;    /* 8px - Buttons */
  --radius-lg:   0.75rem;   /* 12px - Cards */
  --radius-xl:   1rem;      /* 16px - Modals */
  --radius-2xl:  1.5rem;    /* 24px - Bottom sheets */
  --radius-full: 9999px;    /* Pills, audio controls */
  
  /* Shadows - Warm tinted for desert theme */
  --shadow-sm:  0 1px 2px 0 rgba(26, 23, 20, 0.3);
  --shadow-md:  0 4px 6px -1px rgba(26, 23, 20, 0.4);
  --shadow-lg:  0 10px 15px -3px rgba(26, 23, 20, 0.5);
  --shadow-xl:  0 20px 25px -5px rgba(26, 23, 20, 0.6);
  
  /* Glow effects for golden highlights */
  --glow-gold: 0 0 20px rgba(230, 168, 0, 0.3);
  --glow-audio: 0 0 15px rgba(230, 126, 34, 0.4);
}
```

---

## Animation System

### Motion Tokens

```css
:root {
  /* Durations */
  --duration-instant: 50ms;    /* Micro-interactions */
  --duration-fast:    150ms;   /* Buttons, toggles */
  --duration-normal:  250ms;   /* Standard transitions */
  --duration-slow:    400ms;   /* Page transitions */
  --duration-slower:  600ms;   /* Complex animations */
  --duration-reveal:  1000ms;  /* Historical reveals */
  
  /* Easings */
  --ease-linear:    linear;
  --ease-in:        cubic-bezier(0.4, 0, 1, 1);
  --ease-out:       cubic-bezier(0, 0, 0.2, 1);
  --ease-in-out:    cubic-bezier(0.4, 0, 0.2, 1);
  --ease-out-back:  cubic-bezier(0.34, 1.56, 0.64, 1);
  --ease-out-expo:  cubic-bezier(0.16, 1, 0.3, 1);
  --ease-smooth:    cubic-bezier(0.65, 0, 0.35, 1);
  --ease-elastic:   cubic-bezier(0.68, -0.55, 0.265, 1.55);
  
  /* Spring presets (for Moti/React Native Reanimated) */
  --spring-gentle:  { damping: 20, stiffness: 100 };
  --spring-bouncy:  { damping: 10, stiffness: 150 };
  --spring-snappy:  { damping: 15, stiffness: 200 };
}
```

### Key Animations

```css
/* ═══════════════════════════════════════════════════════════════
   AUDIO PLAYING INDICATOR - Pulsing rings
   ═══════════════════════════════════════════════════════════════ */
@keyframes audioPulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

.audio-playing-ring {
  animation: audioPulse 1.5s var(--ease-out) infinite;
}

.audio-playing-ring:nth-child(2) {
  animation-delay: 0.5s;
}

/* ═══════════════════════════════════════════════════════════════
   POI DISCOVERY - Golden shimmer when entering trigger zone
   ═══════════════════════════════════════════════════════════════ */
@keyframes poiDiscovery {
  0% {
    box-shadow: 0 0 0 0 rgba(230, 168, 0, 0.6);
  }
  50% {
    box-shadow: 0 0 20px 10px rgba(230, 168, 0, 0.3);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(230, 168, 0, 0);
  }
}

.poi-discovered {
  animation: poiDiscovery 2s var(--ease-out);
}

/* ═══════════════════════════════════════════════════════════════
   AR TIME TRAVEL - Fade in historical reconstruction
   ═══════════════════════════════════════════════════════════════ */
@keyframes timeTravelReveal {
  0% {
    opacity: 0;
    filter: sepia(100%) blur(10px);
  }
  50% {
    opacity: 0.7;
    filter: sepia(50%) blur(2px);
  }
  100% {
    opacity: 1;
    filter: sepia(0%) blur(0);
  }
}

.ar-reconstruction-enter {
  animation: timeTravelReveal var(--duration-reveal) var(--ease-out-expo);
}

/* ═══════════════════════════════════════════════════════════════
   DOWNLOAD PROGRESS - Hieroglyphic loading
   ═══════════════════════════════════════════════════════════════ */
@keyframes hieroglyphScroll {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 100% 50%;
  }
}

.download-progress-bar {
  background: linear-gradient(90deg, 
    var(--color-primary-700) 0%,
    var(--color-primary-500) 50%,
    var(--color-primary-700) 100%
  );
  background-size: 200% 100%;
  animation: hieroglyphScroll 2s linear infinite;
}

/* ═══════════════════════════════════════════════════════════════
   STAGGERED ENTRANCE - POI list items
   ═══════════════════════════════════════════════════════════════ */
@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.poi-card-enter {
  animation: fadeSlideUp var(--duration-normal) var(--ease-out) forwards;
}

.poi-card-enter:nth-child(1) { animation-delay: 0ms; }
.poi-card-enter:nth-child(2) { animation-delay: 50ms; }
.poi-card-enter:nth-child(3) { animation-delay: 100ms; }
.poi-card-enter:nth-child(4) { animation-delay: 150ms; }
.poi-card-enter:nth-child(5) { animation-delay: 200ms; }
```

### Micro-Interactions

```css
/* ═══════════════════════════════════════════════════════════════
   AUDIO PLAY BUTTON - Press feedback
   ═══════════════════════════════════════════════════════════════ */
.audio-btn-press {
  transition: transform var(--duration-instant) var(--ease-out);
}

.audio-btn-press:active {
  transform: scale(0.95);
}

/* ═══════════════════════════════════════════════════════════════
   MAP MARKER - Hover reveal
   ═══════════════════════════════════════════════════════════════ */
.map-marker {
  transition: 
    transform var(--duration-fast) var(--ease-out-back),
    filter var(--duration-fast) var(--ease-out);
}

.map-marker:hover {
  transform: scale(1.2) translateY(-5px);
  filter: drop-shadow(var(--glow-gold));
}

/* ═══════════════════════════════════════════════════════════════
   LANGUAGE SELECTOR - Flag transition
   ═══════════════════════════════════════════════════════════════ */
.language-flag {
  transition: 
    transform var(--duration-fast) var(--ease-out),
    opacity var(--duration-fast) var(--ease-out);
}

.language-flag-inactive {
  opacity: 0.5;
  transform: scale(0.9);
}

.language-flag-active {
  opacity: 1;
  transform: scale(1);
}

/* ═══════════════════════════════════════════════════════════════
   ITINERARY DAY CARD - Swipe hint
   ═══════════════════════════════════════════════════════════════ */
@keyframes swipeHint {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(-10px); }
}

.itinerary-card-hint {
  animation: swipeHint 2s var(--ease-in-out) infinite;
  animation-delay: 2s;
}
```

---

## Component Examples

### Audio Player Card

```jsx
// React Native / Expo example
import { MotiView, MotiText } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';

const AudioPlayerCard = ({ poi, isPlaying, onPlayPause, progress }) => (
  <MotiView
    from={{ opacity: 0, translateY: 30 }}
    animate={{ opacity: 1, translateY: 0 }}
    transition={{ type: 'timing', duration: 300 }}
    style={styles.card}
  >
    {/* POI Image with gradient overlay */}
    <View style={styles.imageContainer}>
      <Image source={{ uri: poi.imageUrl }} style={styles.image} />
      <LinearGradient
        colors={['transparent', 'rgba(13, 11, 9, 0.9)']}
        style={styles.gradient}
      />
    </View>
    
    {/* Content */}
    <View style={styles.content}>
      <Text style={styles.periodLabel}>{poi.historicalPeriod}</Text>
      <Text style={styles.poiName}>{poi.name}</Text>
      <Text style={styles.duration}>{formatDuration(poi.audioDuration)}</Text>
      
      {/* Audio Controls */}
      <View style={styles.controls}>
        <Pressable 
          onPress={onPlayPause}
          style={({ pressed }) => [
            styles.playButton,
            pressed && styles.playButtonPressed
          ]}
        >
          {isPlaying ? (
            <MotiView
              from={{ scale: 0.8 }}
              animate={{ scale: 1 }}
              transition={{ type: 'spring', damping: 10 }}
            >
              <PauseIcon color="#0D0B09" size={24} />
            </MotiView>
          ) : (
            <PlayIcon color="#0D0B09" size={24} />
          )}
        </Pressable>
        
        {/* Progress Bar */}
        <View style={styles.progressContainer}>
          <MotiView
            animate={{ width: `${progress * 100}%` }}
            transition={{ type: 'timing', duration: 100 }}
            style={styles.progressBar}
          />
        </View>
      </View>
    </View>
    
    {/* Playing Indicator */}
    {isPlaying && (
      <MotiView style={styles.playingIndicator}>
        <MotiView
          from={{ scale: 1, opacity: 1 }}
          animate={{ scale: 1.5, opacity: 0 }}
          transition={{
            type: 'timing',
            duration: 1500,
            loop: true,
          }}
          style={styles.pulseRing}
        />
      </MotiView>
    )}
  </MotiView>
);

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#1A1714',
    borderRadius: 12,
    overflow: 'hidden',
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#3D3832',
  },
  imageContainer: {
    height: 160,
    position: 'relative',
  },
  image: {
    width: '100%',
    height: '100%',
  },
  gradient: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: 80,
  },
  content: {
    padding: 16,
  },
  periodLabel: {
    fontFamily: 'Inter-Bold',
    fontSize: 10,
    color: '#E6A800',
    letterSpacing: 1,
    textTransform: 'uppercase',
    marginBottom: 4,
  },
  poiName: {
    fontFamily: 'PlayfairDisplay-SemiBold',
    fontSize: 20,
    color: '#FAFAF5',
    marginBottom: 4,
  },
  duration: {
    fontFamily: 'JetBrainsMono-Medium',
    fontSize: 12,
    color: '#7A766D',
    marginBottom: 16,
  },
  controls: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  playButton: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#E6A800',
    alignItems: 'center',
    justifyContent: 'center',
  },
  playButtonPressed: {
    transform: [{ scale: 0.95 }],
  },
  progressContainer: {
    flex: 1,
    height: 4,
    backgroundColor: '#3D3832',
    borderRadius: 2,
    overflow: 'hidden',
  },
  progressBar: {
    height: '100%',
    backgroundColor: '#E67E22',
    borderRadius: 2,
  },
  playingIndicator: {
    position: 'absolute',
    top: 12,
    right: 12,
  },
  pulseRing: {
    width: 12,
    height: 12,
    borderRadius: 6,
    backgroundColor: '#E67E22',
  },
});
```

### AR Wayfinding Overlay

```jsx
// ViroReact AR example
import { ViroARScene, ViroImage, ViroText, ViroAnimations } from '@viro-community/react-viro';

ViroAnimations.registerAnimations({
  arrowBounce: {
    properties: {
      positionY: '+=0.1',
    },
    duration: 500,
    easing: 'EaseInOut',
  },
  arrowBounceLoop: [
    ['arrowBounce'],
    ['arrowBounce', { direction: 'reverse' }],
  ],
});

const ARWayfindingScene = ({ destination, onArrival }) => {
  return (
    <ViroARScene>
      {/* Direction Arrow */}
      <ViroImage
        source={require('./assets/ar-arrow.png')}
        position={[0, 0, -2]}
        rotation={[0, destination.bearing, 0]}
        animation={{
          name: 'arrowBounceLoop',
          run: true,
          loop: true,
        }}
        opacity={0.9}
      />
      
      {/* Distance Label */}
      <ViroText
        text={`${destination.distance}m`}
        position={[0, -0.5, -2]}
        style={{
          fontFamily: 'Inter-Bold',
          fontSize: 24,
          color: '#E6A800',
        }}
      />
      
      {/* POI Name */}
      <ViroText
        text={destination.name}
        position={[0, -0.8, -2]}
        style={{
          fontFamily: 'PlayfairDisplay-SemiBold',
          fontSize: 18,
          color: '#FAFAF5',
        }}
      />
    </ViroARScene>
  );
};
```

---

## Iconography

**Primary Icon Set**: Phosphor Icons (warm, friendly, Egyptian-inspired strokes)

**Custom Icons Needed**:
- EgyTour logo (Horus eye + audio wave)
- Site type icons (Pyramid, Temple, Museum, Market)
- Historical period badges
- Audio state indicators
- AR mode toggle
- Offline download indicator
- Lion mascot (Qasr al-Nil bridge lion)

**Icon Sizes**:
```css
--icon-xs: 14px;   /* Inline text */
--icon-sm: 18px;   /* Buttons */
--icon-md: 24px;   /* Navigation */
--icon-lg: 32px;   /* Empty states */
--icon-xl: 48px;   /* Onboarding, AR overlays */
--icon-2xl: 64px;  /* Site category headers */
```

---

## Accessibility

- Minimum touch target: 44×44px
- Color contrast: WCAG AA minimum (4.5:1 for text)
- Focus indicators: 2px solid var(--color-primary-500)
- Reduced motion: Respect `prefers-reduced-motion`
- RTL support: Full Arabic layout support
- Audio transcripts: All audio content has text alternatives
- High contrast mode: Support for vision-impaired users

```css
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* High contrast mode */
@media (prefers-contrast: high) {
  :root {
    --color-text-primary: #FFFFFF;
    --color-text-secondary: #FFFFFF;
    --color-bg-primary: #000000;
    --color-border: #FFFFFF;
  }
}
```

---

## Responsive Breakpoints

```css
:root {
  --breakpoint-sm:  640px;   /* Mobile landscape */
  --breakpoint-md:  768px;   /* Tablet portrait */
  --breakpoint-lg:  1024px;  /* Tablet landscape */
  --breakpoint-xl:  1280px;  /* Desktop */
  --breakpoint-2xl: 1536px;  /* Large desktop */
}
```

---

## Map Styling (Mapbox)

```json
{
  "name": "EgyTour Desert",
  "sources": {},
  "layers": [
    {
      "id": "background",
      "type": "background",
      "paint": {
        "background-color": "#F5F2ED"
      }
    },
    {
      "id": "water",
      "paint": {
        "fill-color": "#0095C4",
        "fill-opacity": 0.6
      }
    },
    {
      "id": "poi-markers",
      "paint": {
        "circle-color": "#E6A800",
        "circle-radius": 8,
        "circle-stroke-color": "#FFFFFF",
        "circle-stroke-width": 2
      }
    }
  ]
}
```
