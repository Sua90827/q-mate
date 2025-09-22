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

  return (
    <button
      onClick={onClick}
      type="button"
      className={`px-3 py-1 rounded-xl transition-colors ${
        active
          ? 'bg-theme-primary text-white'
          : 'border border-theme-secondary text-theme-secondary'
      }`}
      data-theme={theme}
    >
      {label}
    </button>
  );
}
