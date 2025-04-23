using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace EduconnectAPI.Services
{
    public class JwtService
    {
        private readonly string _key;
        private readonly string _issuer;

        //Constructor
        public JwtService(string key, string issuer)
        {
            _key = key;
            _issuer = issuer;
        }

        public string GenerateToken(string email, string role)
        {
            var claims = new[]
            {
                new Claim(ClaimTypes.Email,email),
                new Claim(ClaimTypes.Role,role)
            };

            //Create a secret key
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_key));

            //Signing with the key
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            //Create the token
            var token = new JwtSecurityToken(
                issuer: _issuer,
                audience: _issuer,
                claims: claims,
                expires: DateTime.Now.AddMinutes(30),
                signingCredentials: creds
            );

            //Return the generated token
            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}
