export type ThemeType = 'day' | 'sunset' | 'night';

export function getThemeByHour(hour: number): ThemeType {
  if (hour >= 6 && hour < 16) return 'day';
  if (hour >= 16 && hour < 21) return 'sunset';
  return 'night';
}
