export type ModalType = 'success' | 'errorConfirm' | 'errorNotice' | 'invalidCode' | null;

export interface ModalConfig {
  open: boolean;
  type: ModalType;
  title: React.ReactNode;
  sub?: string;
  isDanger?: boolean;
  onConfirm?: () => void;
}
