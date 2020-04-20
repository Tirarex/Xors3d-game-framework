float4 paramsS = {0.0,0.0,0.0,0.0};//Цветокорекция тени, Shadow Color Corection
float4 paramsM = {0.0,0.0,0.0,0.0};//Цветокорекция полутени(среднего значения), Middle Color Corection
float4 paramsH = {0.0,0.0,0.0,0.0};//Цветокорекция ярких областей, High Color Corection





float4 Blur;

texture BlurTexture;
texture SceneTexture;
texture SmallTexture;

sampler BlurSample = sampler_state
{
	Texture   = <BlurTexture>;
	AddressU  = Clamp;
	AddressV  = Clamp;	
	MinFilter = Linear;
	MagFilter = Linear;
	MipFilter = Linear;
};

sampler SceneSample = sampler_state
{
	Texture   = <SceneTexture>;
	AddressU  = Clamp;
	AddressV  = Clamp;	
	MinFilter = Linear;
	MagFilter = Linear;
	MipFilter = Linear;
};

sampler SmallSample = sampler_state
{
	Texture   = <SmallTexture>;
	AddressU  = Clamp;
	AddressV  = Clamp;	
	MinFilter = Linear;
	MagFilter = Linear;
	MipFilter = Linear;
};

float4 PS(float2 texCoords : TEXCOORD0) : COLOR
{
	float4 ColorSample = tex2D(SceneSample, texCoords);
	
	return ColorSample;
}
#define PI  3.14159265

float Pixels[9] =
{
   -4,
   -3,
   -2,
   -1,
    0,
    1,
    2,
    3,
    4
};
float BlurWeights[9] =
{
   0.05,
   0.09,
   0.12,
   0.15,
   0.16,
   0.15,
   0.12,
   0.09,
   0.05
};

float4 PS2(float2 texCoords : TEXCOORD0) : COLOR
{
	float4 ColorSample = tex2D(SceneSample, texCoords);
	
	float factor = max(ColorSample.x, max(ColorSample.y, ColorSample.z));
	float factorM = (ColorSample.x + ColorSample.y + ColorSample.z)/3;
	float4 shadows = paramsS;
	float4 midtones = paramsM;
	float4 highlights = paramsH;
	float4 Color;
	if(factor < 0.1)	//Shadows
	{
		if(factor > 0.01)
		{
			factor = (factor + 0.09)*10;
			
			shadows.w = shadows.x; //.w is value backup
			shadows.x = ((1 - shadows.w) / 2) * factor;
			shadows.x += shadows.w;	//Adding backup
			
			shadows.w = shadows.y; //.w is value backup
			shadows.y = ((1 - shadows.w) / 2) * factor;
			shadows.y += shadows.w;	//Adding backup
			
			shadows.w = shadows.z; //.w is value backup
			shadows.z = ((1 - shadows.w) / 2) * factor;
			shadows.z += shadows.w;	//Adding backup
		}
		Color = float4(ColorSample.x * shadows.x, ColorSample.y * shadows.y, ColorSample.z * shadows.z, ColorSample.w);	
	}
	else if(factorM >= 0.1 && factorM <= 0.5)	//Middle-tones
	{
		if(factorM > 0.3)
		{
			factorM = (factorM - 0.31)*10;
			
			midtones.w = midtones.x; //.w is value backup
			midtones.x = ((1 - midtones.w) / 2) * factorM;
			midtones.x += midtones.w;	//Adding backup
			
			midtones.w = midtones.y; //.w is value backup
			midtones.y = ((1 - midtones.w) / 2) * factorM;
			midtones.y += midtones.w;	//Adding backup
			
			midtones.w = midtones.z; //.w is value backup
			midtones.z = ((1 - midtones.w) / 2) * factorM;
			midtones.z += midtones.w;	//Adding backup
		}
		else if(factorM <= 0.3)
		{
			factorM = (factorM - 0.1)*10;
			
			midtones.w = midtones.x;
			midtones.x = (1 - midtones.w) - (factorM * ((1 - midtones.w) / 2));
			midtones.x += midtones.w;
			
			midtones.w = midtones.y;
			midtones.y = (1 - midtones.w) - (factorM * ((1 - midtones.w) / 2));
			midtones.y += midtones.w;
			
			midtones.w = midtones.z;
			midtones.z = (1 - midtones.w) - (factorM * ((1 - midtones.w) / 2));
			midtones.z += midtones.w;
		}
		
		Color = float4(ColorSample.x * midtones.x, ColorSample.y * midtones.y, ColorSample.z * midtones.z, ColorSample.w);
	}		
	else if(factorM > 0.5)	//Hightlights
	{
		if(factorM <= 0.8)
		{
			factorM = (factorM - 0.51)*10;
			
			highlights.w = highlights.x;
			highlights.x = (1 - highlights.w) - (factorM * ((1 - highlights.w) / 2));
			highlights.x += highlights.w;
			
			highlights.w = highlights.y;
			highlights.y = (1 - highlights.w) - (factorM * ((1 - highlights.w) / 2));
			highlights.y += highlights.w;
			
			highlights.w = highlights.z;
			highlights.z = (1 - highlights.w) - (factorM * ((1 - highlights.w) / 2));
			highlights.z += highlights.w;
		}
	
		Color = float4(ColorSample.x * highlights.x, ColorSample.y * highlights.y, ColorSample.z * highlights.z, ColorSample.w);
	}else Color = ColorSample;
	return ColorSample;
}
float4 PS3(float2 texCoords : TEXCOORD0) : COLOR
{
    float4 FragmentColor = max(tex2D(BlurSample, texCoords),0);
	return FragmentColor;
}
float CalcGaussianWeight(int sampleDist, float sigma)
{
	float g = 1.0f / sqrt(2.0f * 3.14159 * sigma * sigma);  
	return (g * exp(-(sampleDist * sampleDist) / (2 * sigma * sigma)));
}
float4 blu(float2 texCoords, float2 texScale, float sigma)
{
  
    float samples=32;
    float4 sum ; //результирующий цвет
    float4 msum ; //максима льное значение цвета выборок
    float4 color; 
   float2 direction =(0.1,-0.5);
    
    float delta = 1.0/samples; //порция цвета в одной выборке
    float di = 1.0/(samples-1.0); //вычисляем инкремент
    for (float i=-0.5; i<0.501; i+=di) {
        color = tex2D(BlurSample, texCoords + direction * i); //делаем выборку в заданном направлении
        sum += color * delta; //суммируем цвет
        msum = max(color, msum); //вычисляем максимальное значение цвета
    }

    return  lerp(sum, msum, 0.8); //смешиваем результирующий цвет с максимальным в заданной пропорции
    
    
    
    
    
    
    
}
float4 PS4(float2 texCoords : TEXCOORD0) : COLOR
{
	return blu(texCoords,float2(1, 0),3.5f);
}
float4 PS5(float2 texCoords : TEXCOORD0) : COLOR
{
	return blu(texCoords,float2(0, 1),3.5f);
}
inline float Luminance( float3 c )
{
	return dot( c, float3(0.22, 0.707, 0.071) );
}
float4 PS6(float2 texCoords : TEXCOORD0) : COLOR
{
	const float DELTA = 0.01f;
	float2 TexelSize = float2(1.0f/512.0f,1.0f/512.0f);
 
	float fLogLumSum = 0.0f;

	fLogLumSum += ( Luminance(tex2D(BlurSample, texCoords + TexelSize.xy * float2(-1,-1)).rgb) + DELTA);		
	fLogLumSum += ( Luminance(tex2D(BlurSample, texCoords + TexelSize.xy * float2(1,1)).rgb) + DELTA);		
	fLogLumSum += ( Luminance(tex2D(BlurSample, texCoords + TexelSize.xy * float2(-1,1)).rgb) + DELTA);		
	fLogLumSum += ( Luminance(tex2D(BlurSample, texCoords + TexelSize.xy * float2(1,-1)).rgb) + DELTA);		

	float avg = fLogLumSum / 4.0;
	return float4(avg, avg, avg, avg);
}

float4 PS7(float2 texCoords : TEXCOORD0) : COLOR
{
	
	float cDiffuse	= tex2D(SmallSample,float2(0.0f,0.0f)).r;
	float cBlur		= 0;
	for (float x=0;x<1.0f;x+=0.05f) {
		for (float y=0;y<1.0f;y+=0.05f) {
			cBlur	+= (Luminance(tex2D(BlurSample,float2(x,y)).rgb));
		}
	}
	float lum = cBlur/400.0f;
	lum		= cDiffuse+(lum-cDiffuse)*0.05f;
	return float4(lum,0.0f,0.0f,1.0f);
}
float2 _MainTex_TexelSize = float2(1.0f/16.0f,1.0f/16.0f);
float4 PS9(float2 texCoords : TEXCOORD0) : COLOR
{
	
	float4 tapA = tex2D(BlurSample, texCoords + _MainTex_TexelSize * 0.5);
	float4 tapB = tex2D(BlurSample, texCoords - _MainTex_TexelSize * 0.5);
	float4 tapC = tex2D(BlurSample, texCoords + _MainTex_TexelSize * float2(0.5,-0.5));
	float4 tapD = tex2D(BlurSample, texCoords - _MainTex_TexelSize * float2(0.5,-0.5));
	
	float4 average = (tapA+tapB+tapC+tapD)/4;
	average.y = max(max(tapA.y,tapB.y), max(tapC.y,tapD.y));
	
	return average;
}
// Approximates luminance from an RGB value
float CalcLuminance(float3 color)
{
    return max(dot(color, float3(0.299f, 0.587f, 0.114f)), 0.0001f);
}
float KeyValue = 1.5;
float3 CalcExposedColor(float3 color, float avgLuminance, float threshold, out float exposure)
{    
    exposure = 0;
	// Use geometric mean        
	avgLuminance = max(avgLuminance, 0.0002f);

	float keyValue = KeyValue;
	float linearExposure = (keyValue / avgLuminance);
	exposure = log2(max(linearExposure, 0.0001f));

    exposure -= threshold;
    return exp2(exposure) * color;
}
float WhiteLevel = 8.5;
float Bias = 0.1;
float LuminanceSaturation = 1.0;

float3 ToneMapExponential(float3 color)
{
	float pixelLuminance = CalcLuminance(color);    
    float toneMappedLuminance = 1 - exp(-pixelLuminance / WhiteLevel);
	return toneMappedLuminance * pow(color / pixelLuminance, LuminanceSaturation);
}
float LinearWhite = 1.2;
float ShoulderStrength = 0.22;
float LinearStrength = 0.22;
float LinearAngle = 0.1;
float ToeStrength = 0.1;
float ToeNumerator = 0.01;
float ToeDenominator = 0.3;

float3 U2Func(float3 x)
{
    float A = ShoulderStrength;
    float B = LinearStrength;
    float C = LinearAngle;
    float D = ToeStrength;
    float E = ToeNumerator;
    float F = ToeDenominator;
    return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F)) - E/F;
}

// Applies the Uncharted 2 filmic tone mapping curve
float3 ToneMapFilmicU2(float3 color)
{
    float3 numerator = U2Func(color);        
    float3 denominator = U2Func(LinearWhite);

    return numerator / denominator;
}
float4 PS8(float2 texCoords : TEXCOORD0) : COLOR
{
	float2 avgLum = tex2D(SmallSample, texCoords).xy;
	float4 color = tex2D(SceneSample, texCoords);
	float4 bloom = tex2D(BlurSample, texCoords);
	float exposure = 0;
	

	bloom.xyz = CalcExposedColor(bloom.xyz,avgLum.x,2.0,exposure);
	bloom.xyz = ToneMapExponential(bloom.xyz);
	color.xyz = CalcExposedColor(color.xyz,avgLum.x,0,exposure);
	color.xyz = ToneMapExponential(color.xyz);
	
	return float4(color.xyz+bloom.xyz*1.5,1);
}

technique MainTechnique
{
	pass p0
	{
		PixelShader	= compile ps_2_0 PS();
	}
}
technique Bloom
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS2();
	}
}
technique Tresh
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS3();
	}
}
technique BlurH
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS4();
	}
}
technique BlurV
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS5();
	}
}

technique Log
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS6();
	}
}
technique Downsample
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS9();
	}
}
technique Exp
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS7();
	}
}

technique Tonemap
{
	pass p0
	{
		PixelShader	= compile ps_3_0 PS8();
	}
}
