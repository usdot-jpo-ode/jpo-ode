# Certificate User

## Implicit Certificates
* The certificate holder signs a message with the certified private key and attaches the certificate (contents + reconstruction value)
* The receiver uses the certificate contents, reconstruction value and CA public key to recover the certified public key
* The receiver verifies the signature on the message with the certified public key

Verifying a message signed with an implicit certificate can be done in two steps: extracting the public key from the certificate and verifying the message. To extract the public key from a certificate, the public key from the issuer’s certificate is required. The public key extraction operation is also a double-multiply-and-add. Thus, verifying an implicit certificate chain can be done using V * (n + 1) operations: V for extracting the public key, and V * n for verifying the certificate chain. At the end of the operation, 

[ Cert ID, public key, “successfully verified” ]

is cached. Subsequent messages signed by that certificate can be verified at a cost of V. 

Summarizing, the total cost for verifying reads V * (n + 1) for the first verify, and V for the subsequent ones. This is slightly higher than for the explicit certificates case, but it should be observed that the same hardware as for the explicit case can be used. Recall that implicit certificates have an advantage in terms of size (64 byte in the considered case).

Finally, there is a way to improve the computational performance. Consider the case of a signed message with a certificate chain of length 2, i.e., 

[ message, end-entity (implicit) certificate, known trusted (explicit) CA cert].

One can combine public key extraction and verification into a single operation, a *triple*
multiply and add operation with cost approximately 1.16 * V. So the first verification comes at a
cost of approximately 1.16 * V instead of 2 * V. However, combining operations in this way does
not output the public key, so all subsequent operations (e.g., verifying subsequent messages
signed with the same certificate) also come at a cost of 1.16 * V.