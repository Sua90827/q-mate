import { useThemeStore } from '@/store/useThemeStore';

export default function OptionBtn({
  label,
  active,
  onClick,
}: {
  label: string;
  active: boolean;
  onClick: () => void;
}) {
  const theme = useThemeStore((state) => state.theme);

  let activeClass = '';

  if (active) {
    if (theme === 'sunset') activeClass = 'bg-sunset-active text-white';
    else if (theme === 'night') activeClass = 'bg-night-active text-white';
    else activeClass = 'bg-primary text-secondary';
  }

  return (
    <button
      onClick={onClick}
      type="button"
      className={`px-3 py-1 rounded-xl ${
        active ? activeClass : 'border border-text-secondary text-text-secondary'
      }`}
    >
      {label}
    </button>
  );
}
