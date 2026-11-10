# Liquid Glass design system

## Visual direction
Use the user's music Explore screenshot as the primary reference for material treatment, not as a literal screen layout.

- Deep black / near-black canvas.
- Large, calm content regions and strong typography.
- Functional controls float above content.
- Glass surfaces carry subtle color from content and semantic role.
- A bottom navigation capsule may contain adjacent glass regions.
- A compact media surface may float above navigation.
- Glass edges use restrained highlights and depth; avoid neon outlines and excessive bloom.

Do not turn every card, icon, or text container into glass.

## Material roles
- Regular: text-heavy surfaces, folders, search, settings; stronger diffusion and stable contrast.
- Clear: small controls over wallpaper/photo content; more source visibility and restrained refraction.
- Tinted: navigation and dock surfaces with controlled semantic tint.
- HighContrast: opaque or nearly opaque fallback with visible border.

## Token ownership
All values live in design/tokens. Do not place arbitrary radii, tint alpha, blur radii, or animation constants inside feature screens.

Initial token names are provisional until screenshot/device review:
- Canvas: near-black.
- Surface tint: neutral white at low opacity with optional amber/green/violet accents.
- Edge highlight: thin, low-opacity upper rim.
- Shadow: broad and subtle, never a bright glow.
- Corner geometry: continuous/squircle-like where supported; rounded fallback.
- Spacing scale: 4dp base.
- Touch target: minimum 48dp.

## Components
GlassSurface, GlassDock, GlassNavigation, GlassPanel, GlassIconButton, GlassSlider, GlassMediaCard, GlassBanner.

## Optical behavior
The material may combine source capture, controlled blur/depth diffusion, boundary refraction, restrained chromatic dispersion, specular highlight, dark edge response, tint, and shape masking.

Refraction and dispersion are decorative. No information or affordance may depend on them. Start from Haze Glass Regular/Clear defaults. Tune one parameter at a time after visual and performance comparison. Avoid custom shader code until the library cannot meet a documented requirement.

## Motion
- Interruptible spring transitions for dock selection, folder open/close, and floating panels.
- Pass gesture velocity into settle animations where feasible.
- Short fades only for content that cannot morph.
- Avoid animating large backdrop captures unnecessarily.
- Respect system animator duration scale and reduced-motion preferences.
- Haptics are optional and subtle.

## Accessibility
Maintain readable text over bright and dark wallpaper. Provide reduced-transparency and increased-contrast settings. Keep semantic labels/roles, visible keyboard focus, and selected states that do not rely on tint alone.

## Performance
Start with Haze default/balanced behavior. Do not enable experimental source sharing by default. Prefer stable wallpaper/source capture for static surfaces; use live source sampling only where content behind glass moves. Benchmark release builds on 60Hz and high-refresh devices. Compare visual quality, frame time, and memory together.
