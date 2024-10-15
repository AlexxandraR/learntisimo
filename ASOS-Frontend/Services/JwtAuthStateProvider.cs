using Blazored.LocalStorage;
using Microsoft.AspNetCore.Components.Authorization;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using DTOs.Account;

namespace Services
{
    public class JwtAuthStateProvider : AuthenticationStateProvider
    {
        readonly ILocalStorageService _localStorage;
        readonly HttpClient _httpClient;
        ClaimsPrincipal anonymous = new ClaimsPrincipal(new ClaimsIdentity());

        public JwtAuthStateProvider(ILocalStorageService localStorage, HttpClient httpClient)
        {
            _localStorage = localStorage;
            _httpClient = httpClient;
        }

        public override async Task<AuthenticationState> GetAuthenticationStateAsync()
        {
            try
            {
                string? stringToken = await _localStorage.GetItemAsStringAsync("jwtToken");

                if (string.IsNullOrWhiteSpace(stringToken))
                {
                    return await Task.FromResult(new AuthenticationState(anonymous));
                }

                var claims = GetClaimsFromToken(stringToken);

                var claimsPrincipal = SetClaimPrincipal(claims);

                var roles = claimsPrincipal.FindAll(ClaimTypes.Role).Select(c => c.Value).ToList();
                var other = claimsPrincipal.FindAll(ClaimTypes.Email).Select(c => c.Value).ToList();

                var roleClaim = claimsPrincipal.FindFirst(ClaimTypes.Role)?.Value;

                return await Task.FromResult(new AuthenticationState(claimsPrincipal));

            }
            catch (Exception)
            {
                return await Task.FromResult(new AuthenticationState(anonymous));
            }
        }

        public async Task UpdateAuthenticationState(AuthResponseDto? authResponse)
        {
            ClaimsPrincipal claimsPrincipal = new();
            if (authResponse != null && !string.IsNullOrWhiteSpace(authResponse.AccessToken))
            {
                var userSession = GetClaimsFromToken(authResponse.AccessToken);
                claimsPrincipal = SetClaimPrincipal(userSession);
                await _localStorage.SetItemAsStringAsync("jwtToken", authResponse.AccessToken);
                await _localStorage.SetItemAsStringAsync("refreshToken", authResponse.RefreshToken);
            }
            else
            {
                claimsPrincipal = anonymous;
                await _localStorage.RemoveItemAsync("jwtToken");
                await _localStorage.RemoveItemAsync("refreshToken");
            }

            NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(claimsPrincipal)));
        }

        public ClaimsPrincipal SetClaimPrincipal(UserDto model)
        {
            return new ClaimsPrincipal(new ClaimsIdentity(
                new List<Claim>
                {
                    new Claim(ClaimTypes.Role, model.Role),
                    new Claim(ClaimTypes.Email, model.Email)
                }, "jwtToken"));
        }

        public UserDto GetClaimsFromToken(string jwtToken)
        {
            var handler = new JwtSecurityTokenHandler();
            var token = handler.ReadJwtToken(jwtToken);
            var claims = token.Claims;

            string Email = claims.FirstOrDefault(claim => claim.Type == "sub")?.Value!;
            string Role = claims.FirstOrDefault(claim => claim.Type == "role")?.Value!;

            var extractedRole = Role.Contains("ROLE_") ? Role.Split('"')[3] : string.Empty;

            return new UserDto
            {
                Email = Email,
                Role = extractedRole
            };
        }
    }
}
