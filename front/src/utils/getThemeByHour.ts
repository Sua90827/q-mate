export type ThemeType = 'day' | 'sunset' | 'night';

export function getThemeByHour(hour: number): ThemeType {
  if (hour >= 6 && hour < 18) return 'day';
  if (hour >= 18 && hour < 21) return 'sunset';
  return 'night';
}
