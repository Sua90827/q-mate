interface TextInputProps {
  label?: string;
  type?: string;
  value: string; // ✅ 추가
  validate?: (value: string) => boolean;
  setActive: React.Dispatch<React.SetStateAction<boolean>>;
  onChange: (value: string) => void;
}

export default function TextInput({
  label,
  type = 'text',
  value,
  validate,
  setActive,
  onChange,
}: TextInputProps) {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    onChange(newValue);

    if (newValue.trim() === '') {
      setActive(false);
      return;
    }

    setActive(validate ? validate(newValue) : true);
  };

  return (
    <input
      type={type}
      className="bg-secondary rounded-md text-text-secondary w-[295px] py-2 pl-4 border border-gray"
      placeholder={label}
      value={value}
      onChange={handleChange}
    />
  );
}
