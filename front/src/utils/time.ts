function to12h(hour24: number) {
  const meridiem: 'AM' | 'PM' = hour24 < 12 ? 'AM' : 'PM';
  const h12 = ((hour24 + 11) % 12) + 1; // 0->12, 13->1
  return { h12, meridiem } as const;
}

function to24h(h12: number, meridiem: 'AM' | 'PM') {
  return meridiem === 'AM' ? h12 % 12 : (h12 % 12) + 12;
}

export { to12h, to24h };
