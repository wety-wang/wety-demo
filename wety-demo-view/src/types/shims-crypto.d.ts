declare module 'js-md5' {
  const md5: (message: string) => string
  export default md5
}

declare module 'sm-crypto' {
  export const sm2: {
    doEncrypt: (msg: string, publicKey: string, cipherMode?: number) => string
    doDecrypt: (cipherData: string, privateKey: string, cipherMode?: number) => string
  }
  export const sm3: any
  export const sm4: any
}
