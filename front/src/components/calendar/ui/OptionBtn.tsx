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
    <button
      onClick={onClick}
      type="button"
      className={`px-3 py-1 rounded-xl ${
        active ? 'bg-primary text-secondary' : 'border border-text-secondary text-text-secondary'
      }`}
    >
      {label}
    </button>
  );
}
