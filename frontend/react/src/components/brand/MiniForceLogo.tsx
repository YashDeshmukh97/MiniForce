import React from 'react'

interface MiniForceLogoProps extends React.SVGProps<SVGSVGElement> {
  className?: string
  size?: number | string
}

/**
 * MiniForce Jolly Roger Brand Emblem
 * 
 * A premium, competitive-gaming inspired pirate skull with crossed bones,
 * sculpted silver-white contours, deep graphite shadows, and restrained
 * violet/indigo rim lighting that complements the MiniForce design language.
 */
export const MiniForceLogo: React.FC<MiniForceLogoProps> = ({
  className = 'h-6 w-6',
  size,
  ...props
}) => {
  return (
    <svg
      viewBox="0 0 48 48"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={`shrink-0 select-none transition-transform duration-200 motion-safe:hover:scale-105 ${className}`}
      width={size}
      height={size}
      role="img"
      aria-label="MiniForce Jolly Roger Logo"
      {...props}
    >
      <defs>
        {/* Subtle drop shadow behind skull for 3D depth over crossbones */}
        <filter id="mf-skull-shadow" x="-20%" y="-20%" width="140%" height="140%">
          <feDropShadow dx="0" dy="1.5" stdDeviation="1.5" floodColor="#020617" floodOpacity="0.8" />
        </filter>

        {/* Violet rim glow accent */}
        <filter id="mf-violet-glow" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="0.8" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>

        {/* Crossbones silver gradient */}
        <linearGradient id="mf-bone-grad" x1="8" y1="8" x2="40" y2="40" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#F1F5F9" />
          <stop offset="45%" stopColor="#CBD5E1" />
          <stop offset="70%" stopColor="#94A3B8" />
          <stop offset="100%" stopColor="#475569" />
        </linearGradient>

        {/* Crossbones secondary specular bevel */}
        <linearGradient id="mf-bone-spec" x1="40" y1="8" x2="8" y2="40" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#FFFFFF" stopOpacity="0.9" />
          <stop offset="50%" stopColor="#CBD5E1" stopOpacity="0.4" />
          <stop offset="100%" stopColor="#334155" stopOpacity="0.8" />
        </linearGradient>

        {/* Sculpted silver cranium gradient */}
        <linearGradient id="mf-cranium-grad" x1="24" y1="7" x2="24" y2="39" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#FFFFFF" />
          <stop offset="25%" stopColor="#F1F5F9" />
          <stop offset="60%" stopColor="#CBD5E1" />
          <stop offset="85%" stopColor="#94A3B8" />
          <stop offset="100%" stopColor="#475569" />
        </linearGradient>

        {/* Restrained violet rim lighting gradient */}
        <linearGradient id="mf-rim-violet" x1="12" y1="10" x2="36" y2="38" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#A855F7" stopOpacity="0.8" />
          <stop offset="50%" stopColor="#818CF8" stopOpacity="0.5" />
          <stop offset="100%" stopColor="#6366F1" stopOpacity="0.9" />
        </linearGradient>

        {/* Obsidian eye socket depth */}
        <linearGradient id="mf-socket-depth" x1="24" y1="18" x2="24" y2="28" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#090D16" />
          <stop offset="70%" stopColor="#0F172A" />
          <stop offset="100%" stopColor="#1E1B4B" />
        </linearGradient>

        {/* Teeth gradient */}
        <linearGradient id="mf-teeth-grad" x1="24" y1="31" x2="24" y2="38" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#FFFFFF" />
          <stop offset="60%" stopColor="#E2E8F0" />
          <stop offset="100%" stopColor="#94A3B8" />
        </linearGradient>
      </defs>

      {/* ── 1. CROSSED BONES (LAYER BEHIND CRANIUM) ── */}
      <g opacity="0.95">
        {/* Bone 1: Top-Left to Bottom-Right */}
        {/* Condyles Top-Left */}
        <circle cx="8" cy="8" r="3" fill="url(#mf-bone-grad)" />
        <circle cx="12" cy="6.5" r="2.8" fill="url(#mf-bone-grad)" />
        <circle cx="6.5" cy="12" r="2.8" fill="url(#mf-bone-grad)" />
        {/* Condyles Bottom-Right */}
        <circle cx="40" cy="40" r="3" fill="url(#mf-bone-grad)" />
        <circle cx="41.5" cy="36" r="2.8" fill="url(#mf-bone-grad)" />
        <circle cx="36" cy="41.5" r="2.8" fill="url(#mf-bone-grad)" />
        {/* Shaft 1 */}
        <path
          d="M9 11 L37 39 L39 37 L11 9 Z"
          fill="url(#mf-bone-grad)"
          stroke="#1E293B"
          strokeWidth="0.5"
        />

        {/* Bone 2: Top-Right to Bottom-Left */}
        {/* Condyles Top-Right */}
        <circle cx="40" cy="8" r="3" fill="url(#mf-bone-grad)" />
        <circle cx="36" cy="6.5" r="2.8" fill="url(#mf-bone-grad)" />
        <circle cx="41.5" cy="12" r="2.8" fill="url(#mf-bone-grad)" />
        {/* Condyles Bottom-Left */}
        <circle cx="8" cy="40" r="3" fill="url(#mf-bone-grad)" />
        <circle cx="12" cy="41.5" r="2.8" fill="url(#mf-bone-grad)" />
        <circle cx="6.5" cy="36" r="2.8" fill="url(#mf-bone-grad)" />
        {/* Shaft 2 */}
        <path
          d="M37 11 L9 39 L11 41 L39 13 Z"
          fill="url(#mf-bone-spec)"
          stroke="#1E293B"
          strokeWidth="0.5"
        />

        {/* Violet rim stroke on bones */}
        <circle cx="8" cy="8" r="2.8" stroke="url(#mf-rim-violet)" strokeWidth="0.6" fill="none" opacity="0.6" />
        <circle cx="40" cy="8" r="2.8" stroke="url(#mf-rim-violet)" strokeWidth="0.6" fill="none" opacity="0.6" />
        <circle cx="8" cy="40" r="2.8" stroke="url(#mf-rim-violet)" strokeWidth="0.6" fill="none" opacity="0.6" />
        <circle cx="40" cy="40" r="2.8" stroke="url(#mf-rim-violet)" strokeWidth="0.6" fill="none" opacity="0.6" />
      </g>

      {/* ── 2. SCULPTED CRANIUM & MAXILLA ── */}
      <g filter="url(#mf-skull-shadow)">
        {/* Main Cranium Silhouette */}
        <path
          d="M14 24
             C13 18 16 9 24 9
             C32 9 35 18 34 24
             C34 27 32.5 28.5 31.5 29
             C30.5 29.5 30 31.5 30 33.5
             C30 36 29 37.5 28 37.5
             L20 37.5
             C19 37.5 18 36 18 33.5
             C18 31.5 17.5 29.5 16.5 29
             C15.5 28.5 14 27 14 24 Z"
          fill="url(#mf-cranium-grad)"
          stroke="#0F172A"
          strokeWidth="0.75"
        />

        {/* Violet Edge Rim Lighting along temple & cranium contour */}
        <path
          d="M14.5 23
             C13.5 18.5 16.5 10 24 10
             C31.5 10 34.5 18.5 33.5 23"
          stroke="url(#mf-rim-violet)"
          strokeWidth="1.2"
          strokeLinecap="round"
          fill="none"
          opacity="0.85"
        />

        {/* Forehead & Brow Specular Ridge Highlight */}
        <path
          d="M17 17 C20 14 28 14 31 17"
          stroke="#FFFFFF"
          strokeWidth="0.9"
          strokeLinecap="round"
          fill="none"
          opacity="0.8"
        />
        <path
          d="M19 14.5 C22 13 26 13 29 14.5"
          stroke="#FFFFFF"
          strokeWidth="0.6"
          strokeLinecap="round"
          fill="none"
          opacity="0.5"
        />

        {/* Zygomatic Arch Contours (Cheekbones) */}
        <path
          d="M14.5 24.5 C16 26 18 26.5 19 25"
          stroke="#64748B"
          strokeWidth="0.75"
          strokeLinecap="round"
          fill="none"
          opacity="0.7"
        />
        <path
          d="M33.5 24.5 C32 26 30 26.5 29 25"
          stroke="#64748B"
          strokeWidth="0.75"
          strokeLinecap="round"
          fill="none"
          opacity="0.7"
        />

        {/* ── 3. CHISELED EYE SOCKETS (ORBITS) ── */}
        {/* Left Eye Socket */}
        <path
          d="M17 21
             C18.5 19.8 21.5 20.8 22.2 22
             C22.6 23.5 21.8 26.2 20.2 26.5
             C18.5 26.8 16.5 25 16.2 23.5
             C16 22.5 16.5 21.4 17 21 Z"
          fill="url(#mf-socket-depth)"
          stroke="#090D16"
          strokeWidth="0.75"
        />
        {/* Left Socket Inner Specular/Glint Accent */}
        <circle cx="18.5" cy="22.5" r="0.6" fill="#818CF8" opacity="0.7" />

        {/* Right Eye Socket */}
        <path
          d="M31 21
             C29.5 19.8 26.5 20.8 25.8 22
             C25.4 23.5 26.2 26.2 27.8 26.5
             C29.5 26.8 31.5 25 31.8 23.5
             C32 22.5 31.5 21.4 31 21 Z"
          fill="url(#mf-socket-depth)"
          stroke="#090D16"
          strokeWidth="0.75"
        />
        {/* Right Socket Inner Specular/Glint Accent */}
        <circle cx="29.5" cy="22.5" r="0.6" fill="#818CF8" opacity="0.7" />

        {/* ── 4. PIRIFORM APERTURE (NASAL CAVITY) ── */}
        <path
          d="M24 25.5
             C23.2 27.2 22.2 29.2 22.8 29.8
             C23.3 30.2 24 29.5 24 29.5
             C24 29.5 24.7 30.2 25.2 29.8
             C25.8 29.2 24.8 27.2 24 25.5 Z"
          fill="#090D16"
          stroke="#1E293B"
          strokeWidth="0.5"
        />

        {/* ── 5. TEETH / JAW SECTION ── */}
        <g>
          {/* Upper Dental Shelf */}
          <path
            d="M19 32.5 L29 32.5 L28.5 36.5 L19.5 36.5 Z"
            fill="url(#mf-teeth-grad)"
            stroke="#1E293B"
            strokeWidth="0.6"
          />

          {/* Vertical Tooth Separators */}
          <line x1="21.5" y1="32.5" x2="21.5" y2="36.5" stroke="#0F172A" strokeWidth="0.7" />
          <line x1="24" y1="32.5" x2="24" y2="36.5" stroke="#0F172A" strokeWidth="0.8" />
          <line x1="26.5" y1="32.5" x2="26.5" y2="36.5" stroke="#0F172A" strokeWidth="0.7" />

          {/* Horizontal incisal separation line */}
          <line x1="19.2" y1="34.8" x2="28.8" y2="34.8" stroke="#334155" strokeWidth="0.5" opacity="0.6" />

          {/* Tooth enamel highlights */}
          <rect x="19.8" y="33" width="1.2" height="1.4" rx="0.3" fill="#FFFFFF" opacity="0.75" />
          <rect x="22.2" y="33" width="1.3" height="1.4" rx="0.3" fill="#FFFFFF" opacity="0.85" />
          <rect x="24.5" y="33" width="1.3" height="1.4" rx="0.3" fill="#FFFFFF" opacity="0.85" />
          <rect x="27" y="33" width="1.2" height="1.4" rx="0.3" fill="#FFFFFF" opacity="0.75" />
        </g>
      </g>
    </svg>
  )
}

export default MiniForceLogo
