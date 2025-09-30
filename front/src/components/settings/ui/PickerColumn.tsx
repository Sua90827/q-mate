import { cn } from '@/lib/utils';

export default function PickerColumn<Item extends string | number>({
  items,
  value,
  onChange,
  className,
}: {
  items: Item[];
  value: Item;
  onChange: (value: Item) => void;
  className?: string;
}) {
  const idx = items.indexOf(value);
  const total = items.length;
  const prevIdx = (idx - 1 + total) % total;
  const nextIdx = (idx + 1) % total;
  const prev = items[prevIdx];
  const curr = items[idx];
  const next = items[nextIdx];
  const pad = (item: Item) =>
    typeof item === 'number' ? String(item).padStart(2, '0') : String(item);
  return (
    <div className={cn('h-30 w-20 flex flex-col text-center select-none', className)}>
      <button
        type="button"
        onClick={() => onChange(prev)}
        className="h-10 flex items-center justify-center font-medium text-dash text-lg cursor-pointer"
      >
        {pad(prev)}
      </button>
      <button
        type="button"
        onClick={() => onChange(next)}
        className="h-10 flex items-center justify-center text-xl font-bold text-TimePicker border-y-1 border-theme-primary"
      >
        {pad(curr)}
      </button>
      <button
        type="button"
        onClick={() => onChange(next)}
        className="h-10 flex items-center justify-center font-medium text-dash text-lg cursor-pointer"
      >
        {pad(next)}
      </button>
    </div>
  );
}
