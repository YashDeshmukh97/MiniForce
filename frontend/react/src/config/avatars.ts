import avatar_default from '@/assets/avatars/avatar_default.svg'
import cat_01 from '@/assets/avatars/cat_01.svg'
import controller_01 from '@/assets/avatars/controller_01.svg'
import developer_01 from '@/assets/avatars/developer_01.svg'
import fox_01 from '@/assets/avatars/fox_01.svg'
import frog_01 from '@/assets/avatars/frog_01.svg'
import gaming_01 from '@/assets/avatars/gaming_01.svg'
import koala_01 from '@/assets/avatars/koala_01.svg'
import lightning_01 from '@/assets/avatars/lightning_01.svg'
import lion_01 from '@/assets/avatars/lion_01.svg'
import monkey_01 from '@/assets/avatars/monkey_01.svg'
import panda_01 from '@/assets/avatars/panda_01.svg'
import robot_01 from '@/assets/avatars/robot_01.svg'
import rocket_01 from '@/assets/avatars/rocket_01.svg'
import terminal_01 from '@/assets/avatars/terminal_01.svg'
import tiger_01 from '@/assets/avatars/tiger_01.svg'
import unicorn_01 from '@/assets/avatars/unicorn_01.svg'

export interface AvatarOption {
  id: string
  name: string
  category: 'animal' | 'other'
  image: string
}

export const AVATAR_OPTIONS: AvatarOption[] = [
  // Animal Avatars (9)
  { id: 'cat_01', name: 'Cat', category: 'animal', image: cat_01 },
  { id: 'fox_01', name: 'Fox', category: 'animal', image: fox_01 },
  { id: 'frog_01', name: 'Frog', category: 'animal', image: frog_01 },
  { id: 'koala_01', name: 'Koala', category: 'animal', image: koala_01 },
  { id: 'lion_01', name: 'Lion', category: 'animal', image: lion_01 },
  { id: 'monkey_01', name: 'Monkey', category: 'animal', image: monkey_01 },
  { id: 'panda_01', name: 'Panda', category: 'animal', image: panda_01 },
  { id: 'tiger_01', name: 'Tiger', category: 'animal', image: tiger_01 },
  { id: 'unicorn_01', name: 'Unicorn', category: 'animal', image: unicorn_01 },

  // Other Avatars (8)
  { id: 'controller_01', name: 'Controller', category: 'other', image: controller_01 },
  { id: 'developer_01', name: 'Developer', category: 'other', image: developer_01 },
  { id: 'gaming_01', name: 'Gamer', category: 'other', image: gaming_01 },
  { id: 'lightning_01', name: 'Lightning', category: 'other', image: lightning_01 },
  { id: 'robot_01', name: 'Robot', category: 'other', image: robot_01 },
  { id: 'rocket_01', name: 'Rocket', category: 'other', image: rocket_01 },
  { id: 'terminal_01', name: 'Terminal', category: 'other', image: terminal_01 },
  { id: 'avatar_default', name: 'Default', category: 'other', image: avatar_default },
]

export const getAvatarImage = (id: string | undefined | null): string => {
  if (!id) return avatar_default;
  const avatar = AVATAR_OPTIONS.find(a => a.id === id);
  return avatar ? avatar.image : avatar_default;
}
