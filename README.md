## Usage

VAULT tokens will typically expire. You could have Travis obtain a token for each run by calling the vault API. You’ll still need a credential that you can use to authenticate against VAULT. 

## The Logic 

If you put a VAULT token in your `env vars`  it would need human intervention at or near the TTL. I’m not sure how others are doing it, but you could use the userpass secret engine and store those encrypted variables in Travis.

Then as part of your build, use those to get a short lived VAULT token with the capabilities that your app needs, you can "VAULT Hop" in a sense. 

So perhaps a static encrypted VAULT username/password dedicated to Travis => `/v2/auth/userpass/login/` which would return a `auth.client_token`.
