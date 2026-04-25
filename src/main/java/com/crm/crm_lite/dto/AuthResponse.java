package com.crm.crm_lite.dto;

// FIX: previously only returned token. Frontend had no way to get the current user's id,
// so lead.owner.id === currentUser.id was never true → isOwner always false →
// Edit/Delete/Add Note buttons always hidden even for the owner.
public class AuthResponse {
    public String token;
    public Long   userId;
    public String email;

    public AuthResponse(String token, Long userId, String email) {
        this.token  = token;
        this.userId = userId;
        this.email  = email;
    }
}