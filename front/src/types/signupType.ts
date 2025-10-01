export type VerificationPurpose = 'SIGNUP' | string;

export type SendEmailVerificationBody = {
  email: string;
  purpose: VerificationPurpose;
};

export type ResendEmailVerificationBody = SendEmailVerificationBody;

export type VerifyEmailCodeBody = {
  email: string;
  code: string;
  purpose: VerificationPurpose;
};

export type VerifyEmailCodeResponse = {
  verified: boolean;
  email_verified_token?: string;
};

export type RegisterBody = {
  email: string;
  password: string;
  nickname: string;
  birthDate: string;
  emailVerifiedToken: string;
};

export type RegisterResponse = {
  registered: boolean;
};
