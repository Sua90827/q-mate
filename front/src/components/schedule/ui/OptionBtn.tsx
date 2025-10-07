import { Button } from '@/components/common/Button';
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
  return (
    <Button
      onClick={onClick}
      variant={'icon'}
      type="button"
      className={`px-3 py-1 !rounded-xl h-[32px] transition-colors !text-14 ${
        active ? 'bg-theme-primary text-white' : 'border border-dash text-text-secondary'
      }`}
    >
      {label}
    </Button>
  );
}
