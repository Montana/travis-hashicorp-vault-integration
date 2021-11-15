[![Build Status](https://app.travis-ci.com/Montana/travis-hashicorp-vault-integration.svg?branch=master)](https://app.travis-ci.com/Montana/travis-hashicorp-vault-integration)

## Usage

VAULT tokens will typically expire. You could have Travis obtain a token for each run by calling the VAULT API. You’ll still need a credential that you can use to authenticate against VAULT. 

## The Logic 

If you put a VAULT token in your `env vars`  it would need human intervention at or near the TTL. I’m not sure how others are doing it, but you could use the userpass secret engine and store those encrypted variables in Travis.

Then as part of your build, use those to get a short lived VAULT token with the capabilities that your app needs, you can "VAULT Hop" in a sense. 

So perhaps a static encrypted VAULT username/password dedicated to Travis => `/v2/auth/userpass/login/` which would return a `auth.client_token`.

![image](https://user-images.githubusercontent.com/20936398/141844521-16bf41d6-312d-4f26-a97f-8e0678901b6c.png)


## Why use VAULT? 

HashiCorp VAULT adds an extra layer of security to your tokens, secrets - and lets you manage them. I recommend using VAULT anytime you can. This is just a cursory example of something bigger I'm working on for Travis CI, currently I have HashiCorp Terraform completed (integration), and very close as you can see here getting VAULT integreated. 

## Integration 

```yaml
env:
  - VAULT_ADDR=http://localhost:8200
sudo: required
before_install:
  - wget https://releases.hashicorp.com/vault/0.5.2/vault_0.5.2_linux_amd64.zip
  - unzip vault_0.5.2_linux_amd64.zip
  - ./vault server -dev &
before_script:
  - ./vault auth-enable userpass
  - ./vault write auth/userpass/users/montana password=foo policies=root
  - ./vault auth-enable app-id
  - ./vault write auth/app-id/map/app-id/foo value=root display_name=foo
  - ./vault write auth/app-id/map/user-id/bar value=foo
script: mvn clean test
````
You can see we fetch VAULT, then start writing auth/userpass that's read `foo` `bar`. We then run a Maven test. This Travis CI build is now using VAULT.

<img width="900" alt="Screen Shot 2021-11-15 at 11 44 47 AM" src="https://user-images.githubusercontent.com/20936398/141844213-367ed9a8-47fe-4457-a3de-c60e4b241371.png">

## Role ID's 

RoleID is an identifier that selects the AppRole against which the other credentials are evaluated. When authenticating against this auth method's login endpoint, the RoleID is a required argument (via role_id ) at all times.

![image](https://user-images.githubusercontent.com/20936398/141844618-c5cc712a-13be-4bb0-9abb-db73d1b0f6ab.png)


## Authenticaion 

Via the CLI: 

```bash
curl \
    --request POST \
    --data '{"role_id":"988a9df-...","secret_id":"37b74931..."}' \
    http://127.0.0.1:8200/v1/auth/approle/login
 ```

## SecretID

SecretID is a credential that is required by default for any login (via `secret_id`) and is intended to always be secret. (For advanced usage, requiring a SecretID can be disabled via an AppRole's `bind_secret_id` parameter, allowing machines with only knowledge of the RoleID, or matching other set constraints, to fetch a token). SecretIDs can be created against an AppRole either via generation of a 128-bit purely random UUID by the role itself (Pull mode) or via specific, custom values (Push mode). Similarly to tokens, SecretIDs have properties like usage-limit, TTLs and expirations.

![image](https://user-images.githubusercontent.com/20936398/141845139-ac77c32f-3f8e-4cd3-8559-0b50b9081ca2.png)

## Response Wrapping 

When a response is wrapped, the normal API response from Vault does not contain the original secret, but rather contains a set of information related to the response-wrapping token:

* TTL: The TTL of the response-wrapping token itself
* Token: The actual token value
* Creation Time: The time that the response-wrapping token was created
* Creation Path: The API path that was called in the original request
* Wrapped Accessor: If the wrapped response is an authentication response containing a Vault token, this is the value of the wrapped token's accessor. This is useful for orchestration systems (such as Nomad) to be able to control the lifetime of secrets based on their knowledge of the lifetime of jobs, without having to actually unwrap the response-wrapping token or gain knowledge of the token ID inside.

![image](https://user-images.githubusercontent.com/20936398/141845365-4684417f-f909-4431-b00e-9b0889ed7df3.png)

## Libsodium Sealed Boxes

The format of a sealed box is, `ephemeral_pk ‖ box(m, recipient_pk, ephemeral_sk, nonce=blake2b(ephemeral_pk ‖ recipient_pk))`. Sealed boxes are designed to anonymously send messages to a recipient given its public key. Only the recipient can decrypt these messages, using its private key. While the recipient can verify the integrity of the message, it cannot verify the identity of the sender.

A message is encrypted using an ephemeral key pair, whose secret part is destroyed right after the encryption process. Without knowing the secret key used for a given message, the sender cannot decrypt its own message later. And without additional data, a message cannot be correlated with the identity of its sender.

## Libsodium & VAULT

```c
int sodium_hex2bin(unsigned char * const bin, const size_t bin_maxlen,                   const char * const hex, const size_t hex_len,                   const char * const ignore, size_t * const bin_len,                   const char ** const hex_end);
```

## Authenticated encryption with Libsodium & VAULT using "Sealed Boxes"

Below is myself using `Libsodium, VAULT and Travis, for a full on integration: 

```c
#define MESSAGE ((const unsigned char *) "test")
#define MESSAGE_LEN 4
#define CIPHERTEXT_LEN (crypto_secretbox_MACBYTES + MESSAGE_LEN)
​
unsigned char key[crypto_secretbox_KEYBYTES];
unsigned char nonce[crypto_secretbox_NONCEBYTES];
unsigned char ciphertext[CIPHERTEXT_LEN];
​
crypto_secretbox_keygen(key);
randombytes_buf(nonce, sizeof nonce);
crypto_secretbox_easy(ciphertext, MESSAGE, MESSAGE_LEN, nonce, key);
​
unsigned char decrypted[MESSAGE_LEN];
if (crypto_secretbox_open_easy(decrypted, ciphertext, CIPHERTEXT_LEN, nonce, key) != 0) {
    /* message forged! */
}
```
This operation encrypts a message with a key and a nonce to keep it confidential, then computes an authentication tag. This tag is used to make sure that the message hasn't been tampered with before decrypting it.
