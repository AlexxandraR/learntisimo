using System.IdentityModel.Tokens.Jwt;
using System.Text.Json;
using Microsoft.AspNetCore.Components.Authorization;
using Blazored.LocalStorage;
using System.Net.Http.Headers;
using Interfaces;
using DTOs.Account;
using System.Text;

namespace Services
{
    public class AuthService : IAuthService
    {
		readonly AuthenticationStateProvider _authStateProvider;
		readonly IHttpClientFactory _httpClientFactory;
		readonly ILocalStorageService _localStorage;

		public AuthService(AuthenticationStateProvider authStateProvider, IHttpClientFactory httpClientFactory, ILocalStorageService localStorage)
		{
			_authStateProvider = authStateProvider;
			_httpClientFactory = httpClientFactory;
			_localStorage = localStorage;
        }

		public async Task LoginAsync(LoginDto loginDto)
		{
			var jsonContent = new StringContent(
				JsonSerializer.Serialize(loginDto),
				Encoding.UTF8,
				"application/json");

			var httpClient = _httpClientFactory.CreateClient("API");

			var response = await httpClient.PostAsync("auth/authenticate", jsonContent);

			if (!response.IsSuccessStatusCode)
			{
				throw new UnauthorizedAccessException("Forbidden access.");
			}

			string jsonResponse = await response.Content.ReadAsStringAsync();

			AuthResponseDto? authResponse = JsonSerializer.Deserialize<AuthResponseDto>(jsonResponse);

			var handler = new JwtSecurityTokenHandler();

			if (authResponse == null || !handler.CanReadToken(authResponse.AccessToken))
			{
				throw new ArgumentException("Invalid token.");
			}

			var jsonToken = handler.ReadToken(authResponse.AccessToken) as JwtSecurityToken;

			var customAuthStateProvider = (JwtAuthStateProvider)_authStateProvider;
			await customAuthStateProvider.UpdateAuthenticationState(authResponse);
		}

		public async Task RegisterAsync(RegisterDto registerDto)
		{
			//TODO: Email must be unique

			var jsonContent = new StringContent(
				JsonSerializer.Serialize(registerDto),
				Encoding.UTF8,
				"application/json");

			var httpClient = _httpClientFactory.CreateClient("API");

			var response = await httpClient.PostAsync("auth/register", jsonContent);

			if (!response.IsSuccessStatusCode)
			{
				throw new UnauthorizedAccessException("Forbidden access.");
			}
		}

		public async Task LogoutAsync()
		{
            var httpClient = _httpClientFactory.CreateClient("API");

            var response = await httpClient.PostAsync("auth/logout", null);

            if (!response.IsSuccessStatusCode)
            {
                throw new HttpRequestException("Unable to invalidate JWT token server-side.");

            }

            var customAuthStateProvider = (JwtAuthStateProvider)_authStateProvider;
            await customAuthStateProvider.UpdateAuthenticationState(null!);
        }

		public async Task RefreshTokenAsync()
        {
            var httpClient = _httpClientFactory.CreateClient("API");

			string? refreshToken = await this.GetRefreshTokenAsync();

            if (!string.IsNullOrWhiteSpace(refreshToken))
            {
                httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", refreshToken);
            }

            var response = await httpClient.PostAsync("auth/refresh", null);

            if (!response.IsSuccessStatusCode)
            {
                throw new UnauthorizedAccessException("Forbidden access.");
            }

            string jsonResponse = await response.Content.ReadAsStringAsync();

            AuthResponseDto? authResponse = JsonSerializer.Deserialize<AuthResponseDto>(jsonResponse);

            var handler = new JwtSecurityTokenHandler();

            if (authResponse == null || !handler.CanReadToken(authResponse.AccessToken))
            {
                throw new ArgumentException("Invalid token.");
            }

            var jsonToken = handler.ReadToken(authResponse.AccessToken) as JwtSecurityToken;

            var customAuthStateProvider = (JwtAuthStateProvider)_authStateProvider;
            await customAuthStateProvider.UpdateAuthenticationState(authResponse);
        }

        public async Task<string?> GetJwtTokenAsync()
		{
            string? jwtToken = await _localStorage.GetItemAsStringAsync("jwtToken");

			return jwtToken;
        }

        public async Task<string?> GetRefreshTokenAsync()
        {
            string? refreshToken = await _localStorage.GetItemAsStringAsync("refreshToken");

            return refreshToken;
        }
    }
}
