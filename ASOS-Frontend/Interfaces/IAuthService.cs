using DTOs.Account;

namespace Interfaces
{
    public interface IAuthService
    {
        Task LoginAsync(LoginDto loginDto);

        Task RegisterAsync(RegisterDto registerDto);

        Task LogoutAsync();

        Task RefreshTokenAsync();

        Task<string?> GetJwtTokenAsync();

        Task<string?> GetRefreshTokenAsync();
    }
}
