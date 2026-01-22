🎨 Styles Overview
Background & Layout
Style	Description
Dark Gradient Background	linear-gradient(135deg, #0f0f23, #1a1a2e, #16213e) - Deep space theme
Flexbox Centering	Content is perfectly centered using display: flex, align-items: center, justify-content: center
Font	'Inter' with system font fallbacks
Star Background Effect
Created using multiple radial-gradient layers to simulate stars
Two star layers (.stars and .stars2) with different sizes and opacity for depth
Floating Shapes
Three blurred circular shapes (.shape-1, .shape-2, .shape-3)
Purple/indigo gradient with blur(40px) filter for soft glow effect
Different sizes (200px, 300px, 400px) positioned around the viewport
Buttons
Button Type	Style
Primary	Purple gradient (#6366f1 → #8b5cf6) with glow shadow
Secondary	Glassmorphism effect with backdrop-filter: blur(10px)
Outline	Transparent with subtle border
Typography
Error Code: Huge font (8rem), rainbow gradient text (#6366f1 → #a855f7 → #ec4899) using background-clip: text
Title: Bold white text with letter-spacing
Description: Muted gray (#94a3b8) for readability
✨ Animations (4 total)
Animation	Element	Duration	Effect
twinkle	.stars, .stars2	4s / 6s	Opacity fades 0.3 → 0.8 → 0.3 (stars twinkling)
float	.shape-1, .shape-2, .shape-3	6s / 8s / 10s	Vertical movement (-30px) with slight rotation (10deg)
pulse	.error-icon	2s	Scale 1 → 1.1 → 1 with opacity change
glow	.error-code	3s	Drop shadow intensity oscillates (20px → 40px)
Keyframe Definitions:
css
@keyframes twinkle { 0%, 100% { opacity: 0.3; } 50% { opacity: 0.8; } }
@keyframes float { 0%, 100% { transform: translateY(0) rotate(0deg); } 50% { transform: translateY(-30px) rotate(10deg); } }
@keyframes pulse { 0%, 100% { transform: scale(1); opacity: 1; } 50% { transform: scale(1.1); opacity: 0.8; } }
@keyframes glow { 0%, 100% { filter: drop-shadow(0 0 20px rgba(99, 102, 241, 0.3)); } 50% { filter: drop-shadow(0 0 40px rgba(168, 85, 247, 0.5)); } }
🔄 Hover Transitions
All buttons have transition: all 0.3s ease with transform: translateY(-2px) on hover for a lift effect.