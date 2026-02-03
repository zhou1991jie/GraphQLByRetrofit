# GraphQL 配置与使用指南

本文档详细讲解如何在 Android 项目中配置和使用 GraphQL，包括完整的代码结构、配置步骤和工作原理。按照本文档的内容，你可以直接在项目中实现一套完整的 GraphQL 集成方案。

## 一、项目结构

首先，让我们了解一下完整的项目结构：

```
app/src/main/java/com/example/grapqldemo6/
├── data/
│   ├── model/             # 数据模型
│   ├── ApiClient.kt       # API 客户端配置
│   ├── ApiConstants.kt    # API 常量
│   ├── GraphQLError.kt     # GraphQL 错误模型
│   ├── GraphQLQueries.kt  # GraphQL 查询语句
│   ├── GraphQLRequest.kt  # GraphQL 请求模型
│   ├── GraphQLResponse.kt # GraphQL 响应模型
│   ├── PokemonRepository.kt # 数据仓库
├── domain/
│   ├── usecase/           # 用例
│   ├── PokemonApiService.kt # API 服务接口
├── presenter/
│   ├── PokemonState.kt    # 状态管理
│   ├── PokemonViewModel.kt # 视图模型
├── ui/
│   ├── components/        # UI 组件
│   ├── screen/            # 屏幕
```

## 二、GraphQLQueries - 查询语句配置

### 1. 核心作用

`GraphQLQueries` 用于集中管理所有的 GraphQL 查询语句，使用 `object` 单例模式确保查询语句只被创建一次，提高性能。

### 2. 实现代码

```kotlin
package com.example.grapqldemo6.data

object GraphQLQueries {
    // 搜索宝可梦物种的查询语句
    // 定义了三个变量：
    // $name: 搜索名称，使用 _ilike 进行模糊匹配
    // $limit: 返回结果数量限制
    // $offset: 分页偏移量
    val SEARCH_POKEMON_SPECIES = """
        query searchPokemonSpecies(${'$'}name: String, ${'$'}limit: Int, ${'$'}offset: Int) {
            // 查询 pokemon_v2_pokemonspecies 表
            // where 子句：name 字段使用 _ilike 操作符进行模糊匹配
            // limit 和 offset 用于分页
            pokemon_v2_pokemonspecies(where: {name: {_ilike: ${'$'}name}}, limit: ${'$'}limit, offset: ${'$'}offset) {
                // 选择需要的字段
                id
                name
                capture_rate
                // 嵌套查询宝可梦颜色信息
                pokemon_v2_pokemoncolor {
                    id
                    name
                }
                // 嵌套查询宝可梦基本信息
                pokemon_v2_pokemons {
                    id
                    name
                    // 嵌套查询宝可梦能力
                    pokemon_v2_pokemonabilities {
                        id
                        // 嵌套查询能力详情
                        pokemon_v2_ability {
                            name
                        }
                    }
                }
            }
        }
    """.trimIndent()
}
```

### 3. 设计原理

- **集中管理**：所有查询语句集中在一个文件中，便于维护和管理
- **可读性**：使用多行字符串（triple quotes）编写查询，保持 GraphQL 语法的可读性
- **类型安全**：虽然查询语句本身是字符串，但通过明确的变量定义和字段选择，提高了代码的可维护性
- **灵活性**：通过变量参数，可以动态构建查询，适应不同的搜索条件

### 4. 扩展方法

当需要添加新的查询时，只需在 `GraphQLQueries` 对象中添加新的查询语句：

```kotlin
object GraphQLQueries {
    // 现有查询...
    
    // 新增：获取单个宝可梦详情的查询
    val GET_POKEMON_DETAIL = """
        query getPokemonDetail(${'$'}id: Int!) {
            pokemon_v2_pokemon(where: {id: {_eq: ${'$'}id}}) {
                id
                name
                height
                weight
                // 其他字段...
            }
        }
    """.trimIndent()
}
```

## 三、ApiClient - 网络客户端配置

### 1. 核心作用

`ApiClient` 负责配置和创建 Retrofit 网络客户端，包括 OkHttp 配置、拦截器设置和服务创建。

### 2. 实现代码

```kotlin
package com.example.grapqldemo6.data

import com.example.grapqldemo6.domain.PokemonApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // 提供配置好的 OkHttpClient
    private fun provideOkHttpClient(): OkHttpClient {
        // 创建日志拦截器，用于调试网络请求
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 记录完整的请求和响应
        }

        return OkHttpClient.Builder()
            // 设置连接超时时间
            .connectTimeout(ApiConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            // 设置读取超时时间
            .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            // 设置写入超时时间
            .writeTimeout(ApiConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            // 添加日志拦截器
            .addInterceptor(loggingInterceptor)
            // 添加自定义拦截器，用于添加统一的请求头
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Accept", "application/json") // 添加 Accept 头
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    // 使用 lazy 委托，确保 retrofit 实例只在首次使用时创建
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            // 设置基础 URL
            .baseUrl(ApiConstants.BASE_URL)
            // 设置 OkHttpClient
            .client(provideOkHttpClient())
            // 添加 Gson 转换器，用于自动序列化和反序列化 JSON
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 使用 lazy 委托，确保 pokemonService 实例只在首次使用时创建
    val pokemonService: PokemonApiService by lazy {
        retrofit.create(PokemonApiService::class.java)
    }
}
```

### 3. 设计原理

- **单例模式**：使用 `object` 单例模式确保 ApiClient 只被创建一次
- **延迟初始化**：使用 `lazy` 委托，确保 Retrofit 和服务实例只在首次使用时创建，提高应用启动速度
- **配置集中**：所有网络相关的配置都集中在一个地方，便于管理和修改
- **拦截器链**：通过添加多个拦截器，可以实现日志记录、请求头添加、认证等功能

### 4. ApiConstants 配置

```kotlin
package com.example.grapqldemo6.data

object ApiConstants {
    // GraphQL API 的基础 URL
    const val BASE_URL = "https://beta.pokeapi.co/graphql/v1beta/"
    
    // 网络超时时间（秒）
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
```

## 三、GraphQLRequest 和 GraphQLResponse - 请求响应模型

### 1. GraphQLRequest - 请求模型

```kotlin
package com.example.grapqldemo6.data

// GraphQL 请求模型
// 包含两个字段：
// query: GraphQL 查询语句
// variables: 查询变量，可选

data class GraphQLRequest(
    val query: String,           // GraphQL 查询语句
    val variables: Map<String, Any>? = null // 查询变量
)
```

### 2. GraphQLResponse - 响应模型

```kotlin
package com.example.grapqldemo6.data

// GraphQL 响应模型
// 使用泛型 T 支持不同类型的数据响应
// 包含两个字段：
// data: 响应数据，可选
// errors: 错误信息，可选

data class GraphQLResponse<T>(
    val data: T? = null,           // 响应数据
    val errors: List<GraphQLError>? = null // 错误信息
)
```

### 3. GraphQLError - 错误模型

```kotlin
package com.example.grapqldemo6.data

// GraphQL 错误模型
// 用于解析 GraphQL 响应中的错误信息
data class GraphQLError(
    val message: String,           // 错误消息
    val locations: List<Location>? = null, // 错误位置
    val path: List<String>? = null  // 错误路径
)

// 错误位置模型
data class Location(
    val line: Int,                 // 错误行号
    val column: Int                // 错误列号
)
```

## 四、PokemonApiService - API 服务接口

### 1. 核心作用

`PokemonApiService` 定义了与 GraphQL API 通信的接口，使用 Retrofit 注解定义请求方法。

### 2. 实现代码

```kotlin
package com.example.grapqldemo6.domain

import com.example.grapqldemo6.data.GraphQLRequest
import com.example.grapqldemo6.data.GraphQLResponse
import com.example.grapqldemo6.data.model.PokemonData
import retrofit2.http.Body
import retrofit2.http.POST

// GraphQL API 服务接口
// 使用 Retrofit 注解定义请求方法
interface PokemonApiService {
    // POST 请求，路径为 "."（相对于 BASE_URL）
    // 使用 @Body 注解传递 GraphQLRequest 对象
    // 使用 suspend 关键字支持协程
    @POST(".")
    suspend fun searchPokemon(
        @Body request: GraphQLRequest // 请求体
    ): retrofit2.Response<GraphQLResponse<PokemonData>> // 响应类型
}
```

### 3. 设计原理

- **接口分离**：将 API 服务定义为接口，便于测试和替换实现
- **协程支持**：使用 `suspend` 关键字，支持 Kotlin 协程，简化异步代码
- **类型安全**：使用泛型 `GraphQLResponse<PokemonData>`，确保类型安全
- **Retrofit 集成**：利用 Retrofit 的注解系统，简化网络请求的定义

## 五、PokemonRepository - 数据仓库

### 1. 核心作用

`PokemonRepository` 是数据访问的中心，负责处理与 API 的通信、数据转换和错误处理，为上层提供干净的数据访问接口。

### 2. 实现代码

```kotlin
package com.example.grapqldemo6.data

import com.example.grapqldemo6.data.model.PokemonData
import com.example.grapqldemo6.domain.PokemonApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

// 数据仓库，负责处理数据访问逻辑
// 使用 @Inject 注解支持依赖注入
class PokemonRepository @Inject constructor(
    private val pokemonApiService: PokemonApiService // 注入 API 服务
) {

    // 分页大小
    private val pageSize = 10

    // 搜索宝可梦
    // 参数：
    // name: 搜索名称
    // page: 页码，默认为 0
    // 返回：Result<PokemonData>，包含成功或失败的结果
    suspend fun searchPokemonByName(name: String, page: Int = 0): Result<PokemonData> {
        // 在 IO 线程中执行网络请求
        return withContext(Dispatchers.IO) {
            try {
                // 1. 获取查询语句
                val query = GraphQLQueries.SEARCH_POKEMON_SPECIES
                
                // 2. 构建查询变量
                // 使用 % 作为通配符，实现模糊匹配
                // limit 和 offset 用于分页
                val variables = mapOf(
                    "name" to "%$name%",  // 模糊匹配
                    "limit" to pageSize,   // 限制返回数量
                    "offset" to page * pageSize // 计算偏移量
                )

                // 3. 创建 GraphQL 请求
                val request = GraphQLRequest(
                    query = query,     // 查询语句
                    variables = variables // 查询变量
                )

                // 4. 执行网络请求
                val response: Response<GraphQLResponse<PokemonData>> = 
                    pokemonApiService.searchPokemon(request)

                // 5. 处理响应
                if (response.isSuccessful) {
                    val body = response.body()
                    // 检查是否有 GraphQL 错误
                    if (body?.errors.isNullOrEmpty()) {
                        // 成功：返回数据
                        Result.success(body?.data ?: PokemonData(emptyList()))
                    } else {
                        // GraphQL 错误
                        val errorMessage = body?.errors?.joinToString { it.message }
                            ?: "GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }
                } else {
                    // HTTP 错误
                    Result.failure(Exception("HTTP error: ${response.code()}"))
                }
            } catch (e: Exception) {
                // 网络或其他错误
                Result.failure(e)
            }
        }
    }
}
```

### 3. 设计原理

- **依赖注入**：使用 `@Inject` 注解，支持依赖注入，提高代码的可测试性和可维护性
- **线程管理**：使用 `withContext(Dispatchers.IO)`，确保网络请求在 IO 线程中执行，避免阻塞主线程
- **错误处理**：全面的错误处理，包括 HTTP 错误、GraphQL 错误和网络异常
- **结果封装**：使用 `Result` 类封装成功或失败的结果，提供统一的错误处理机制
- **分页支持**：通过 `page` 参数和 `offset` 计算，支持分页加载

## 六、PokemonState - 状态管理

### 1. 核心作用

`PokemonState` 是一个密封类，用于管理宝可梦数据的各种状态，包括空闲、加载中、成功和错误状态。

### 2. 实现代码

```kotlin
package com.example.grapqldemo6.presenter

import com.example.grapqldemo6.data.model.PokemonSpecies

// 密封类，用于状态管理
// 支持四种状态：
// Idle: 初始状态
// Loading: 加载中状态
// Success: 成功状态，包含数据
// Error: 错误状态，包含错误信息
sealed class PokemonState {
    // 初始状态
    object Idle : PokemonState()
    
    // 加载中状态
    object Loading : PokemonState()
    
    // 成功状态
    // 包含以下字段：
    // results: 宝可梦列表
    // hasNextPage: 是否有下一页
    // hasSearched: 是否已搜索
    // isNewSearch: 是否是新搜索
    // isLoadingMore: 是否正在加载更多
    // loadMoreError: 加载更多是否出错
    data class Success(
        val results: List<PokemonSpecies>,    // 宝可梦列表
        val hasNextPage: Boolean,             // 是否有下一页
        val hasSearched: Boolean,             // 是否已搜索
        val isNewSearch: Boolean = true,      // 是否是新搜索
        val isLoadingMore: Boolean = false,   // 是否正在加载更多
        val loadMoreError: Boolean = false    // 加载更多是否出错
    ) : PokemonState()
    
    // 错误状态
    // 包含错误消息
    data class Error(val message: String) : PokemonState()
}
```

### 3. 设计原理

- **密封类**：使用 `sealed class`，确保所有状态都在一个地方定义，提高代码的可维护性
- **状态封装**：每个状态都封装了相应的数据，便于在 UI 中处理不同的状态
- **扩展性**：可以轻松添加新的状态类型
- **类型安全**：使用不同的状态类，确保类型安全

## 七、PokemonViewModel - 视图模型

### 1. 核心作用

`PokemonViewModel` 负责处理业务逻辑，管理 `PokemonState`，并与 `PokemonRepository` 交互。

### 2. 实现代码

```kotlin
package com.example.grapqldemo6.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grapqldemo6.data.PokemonRepository
import com.example.grapqldemo6.data.model.PokemonSpecies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 视图模型，负责处理业务逻辑和状态管理
class PokemonViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    // 内部可变状态流
    private val _state = MutableStateFlow<PokemonState>(PokemonState.Idle)
    
    // 对外暴露不可变状态流
    val state: StateFlow<PokemonState> = _state

    // 当前搜索关键词
    private var currentSearchQuery = ""
    
    // 当前页码
    private var currentPage = 0

    // 搜索宝可梦
    // 参数：
    // query: 搜索关键词
    fun searchPokemon(query: String) {
        // 重置页码
        currentPage = 0
        currentSearchQuery = query
        
        // 发送加载中状态
        _state.value = PokemonState.Loading

        // 在 viewModelScope 中执行协程
        viewModelScope.launch {
            try {
                // 调用仓库方法搜索宝可梦
                val result = pokemonRepository.searchPokemonByName(query, currentPage)
                
                // 处理结果
                result.onSuccess {
                    // 检查是否有下一页
                    val hasNextPage = it.pokemonSpecies.size == 10 // 假设每页 10 条
                    
                    // 发送成功状态
                    _state.value = PokemonState.Success(
                        results = it.pokemonSpecies,
                        hasNextPage = hasNextPage,
                        hasSearched = true,
                        isNewSearch = true
                    )
                }.onFailure {
                    // 发送错误状态
                    _state.value = PokemonState.Error(it.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                // 发送错误状态
                _state.value = PokemonState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // 加载更多宝可梦
    fun loadMorePokemon() {
        // 检查当前状态
        val currentState = _state.value
        if (currentState !is PokemonState.Success || currentState.isLoadingMore) {
            return
        }

        // 检查是否有下一页
        if (!currentState.hasNextPage) {
            return
        }

        // 增加页码
        currentPage++

        // 发送加载更多状态
        _state.value = currentState.copy(
            isLoadingMore = true,
            loadMoreError = false
        )

        // 在 viewModelScope 中执行协程
        viewModelScope.launch {
            try {
                // 调用仓库方法搜索宝可梦
                val result = pokemonRepository.searchPokemonByName(currentSearchQuery, currentPage)
                
                // 处理结果
                result.onSuccess {
                    val newResults = currentState.results + it.pokemonSpecies
                    val hasNextPage = it.pokemonSpecies.size == 10
                    
                    // 发送成功状态
                    _state.value = PokemonState.Success(
                        results = newResults,
                        hasNextPage = hasNextPage,
                        hasSearched = true,
                        isNewSearch = false,
                        isLoadingMore = false
                    )
                }.onFailure {
                    // 发送错误状态
                    _state.value = currentState.copy(
                        isLoadingMore = false,
                        loadMoreError = true
                    )
                }
            } catch (e: Exception) {
                // 发送错误状态
                _state.value = currentState.copy(
                    isLoadingMore = false,
                    loadMoreError = true
                )
            }
        }
    }
}
```

### 3. 设计原理

- **状态管理**：使用 `StateFlow` 管理状态，提供响应式的状态更新
- **协程支持**：使用 `viewModelScope` 执行协程，确保协程在 ViewModel 销毁时被取消
- **错误处理**：全面的错误处理，确保应用不会崩溃
- **分页支持**：实现了加载更多功能，支持分页浏览
- **状态转换**：清晰的状态转换逻辑，确保 UI 能够正确反映当前状态

## 八、完整的数据流

### 1. 搜索宝可梦的数据流

1. **UI 触发搜索**：用户在搜索框中输入文本并点击搜索按钮
2. **ViewModel 处理搜索**：`PokemonViewModel.searchPokemon()` 被调用
3. **状态更新**：ViewModel 发送 `Loading` 状态
4. **仓库执行请求**：`PokemonRepository.searchPokemonByName()` 执行网络请求
5. **构建 GraphQL 请求**：
   - 获取查询语句 `GraphQLQueries.SEARCH_POKEMON_SPECIES`
   - 构建查询变量 `{"name": "%$name%", "limit": 10, "offset": 0}`
   - 创建 `GraphQLRequest`
6. **执行网络请求**：`pokemonApiService.searchPokemon()` 发送 POST 请求
7. **处理响应**：
   - 解析 `GraphQLResponse`
   - 检查是否有错误
   - 封装为 `Result`
8. **更新状态**：ViewModel 根据 `Result` 更新状态为 `Success` 或 `Error`
9. **UI 更新**：Compose 监听 `state` 变化，更新 UI 显示

### 2. 加载更多的数据流

1. **UI 触发加载**：用户滚动到列表底部，触发加载更多
2. **ViewModel 处理加载**：`PokemonViewModel.loadMorePokemon()` 被调用
3. **状态更新**：ViewModel 发送 `Success(isLoadingMore = true)` 状态
4. **仓库执行请求**：`PokemonRepository.searchPokemonByName()` 执行网络请求
5. **执行网络请求**：与搜索流程相同
6. **处理响应**：与搜索流程相同
7. **更新状态**：ViewModel 将新数据添加到现有数据中，更新状态
8. **UI 更新**：Compose 监听 `state` 变化，更新 UI 显示

## 九、使用示例

### 1. 在 UI 中使用 ViewModel

```kotlin
@Composable
fun HomeScreen() {
    val viewModel: PokemonViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { viewModel.searchPokemon(searchQuery) }
        )

        when (val currentState = state) {
            is PokemonState.Idle -> {
                Text("Enter a name to search")
            }
            is PokemonState.Loading -> {
                CircularProgressIndicator()
            }
            is PokemonState.Success -> {
                PokemonList(
                    pokemonList = currentState.results,
                    onLoadMore = {
                        if (currentState.hasNextPage && !currentState.isLoadingMore) {
                            viewModel.loadMorePokemon()
                        }
                    },
                    isLoadingMore = currentState.isLoadingMore,
                    loadMoreError = currentState.loadMoreError
                )
            }
            is PokemonState.Error -> {
                Text("Error: ${currentState.message}")
                Button(onClick = { viewModel.searchPokemon(searchQuery) }) {
                    Text("Retry")
                }
            }
        }
    }
}
```

### 2. 数据模型示例

```kotlin
// PokemonData.kt
package com.example.grapqldemo6.data.model

data class PokemonData(
    val pokemonSpecies: List<PokemonSpecies>
)

// PokemonSpecies.kt
package com.example.grapqldemo6.data.model

data class PokemonSpecies(
    val id: Int,
    val name: String,
    val capture_rate: Int,
    val pokemon_v2_pokemoncolor: PokemonColor,
    val pokemon_v2_pokemons: List<Pokemon>
)

// Pokemon.kt
package com.example.grapqldemo6.data.model

data class Pokemon(
    val id: Int,
    val name: String,
    val pokemon_v2_pokemonabilities: List<PokemonAbility>
)

// PokemonColor.kt
package com.example.grapqldemo6.data.model

data class PokemonColor(
    val id: Int,
    val name: String
)

// PokemonAbility.kt
package com.example.grapqldemo6.data.model

data class PokemonAbility(
    val id: Int,
    val pokemon_v2_ability: Ability
)

// Ability.kt
package com.example.grapqldemo6.data.model

data class Ability(
    val name: String
)
```

## 十、依赖配置

### 1. 添加必要的依赖

在 `app/build.gradle.kts` 文件中添加以下依赖：

```kotlin
dependencies {
    // Retrofit
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.squareup.retrofit2:converter-gson:2.9.0"
    
    // OkHttp
    implementation "com.squareup.okhttp3:okhttp:4.9.3"
    implementation "com.squareup.okhttp3:logging-interceptor:4.9.3"
    
    // Kotlin Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
    
    // Hilt (可选，用于依赖注入)
    implementation "com.google.dagger:hilt-android:2.44"
    kapt "com.google.dagger:hilt-android-compiler:2.44"
    implementation "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0"
    kapt "androidx.hilt:hilt-compiler:1.0.0"
    
    // Jetpack Compose (可选，用于 UI)
    implementation "androidx.compose.ui:ui:1.3.0"
    implementation "androidx.compose.material:material:1.3.0"
    implementation "androidx.compose.ui:ui-tooling-preview:1.3.0"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"
}
```

### 2. 配置 Hilt (可选)

在 `Application` 类中添加 `@HiltAndroidApp` 注解：

```kotlin
package com.example.grapqldemo6

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokemonApplication : Application() {}
```

在 `AndroidManifest.xml` 中注册：

```xml
<application
    android:name=".PokemonApplication"
    <!-- 其他配置 -->
>
</application>
```

## 十一、总结

通过本文档的配置，你已经实现了一套完整的 GraphQL 集成方案，包括：

1. **集中管理的查询语句**：使用 `GraphQLQueries` 管理所有查询
2. **完整的网络配置**：使用 `ApiClient` 配置 Retrofit 和 OkHttp
3. **类型安全的请求响应**：使用 `GraphQLRequest` 和 `GraphQLResponse`
4. **层次分明的数据访问**：使用 `PokemonRepository` 封装数据访问逻辑
5. **响应式的状态管理**：使用 `PokemonState` 和 `PokemonViewModel`
6. **清晰的数据流**：从 UI 到 ViewModel 到 Repository 到 API 的完整流程

这套方案具有以下优点：

- **模块化**：代码结构清晰，易于维护
- **可扩展性**：可以轻松添加新的查询和功能
- **错误处理**：全面的错误处理机制
- **性能优化**：使用单例、延迟初始化等技术提高性能
- **类型安全**：使用 Kotlin 的类型系统确保类型安全

按照本文档的内容，你可以直接在项目中实现这套完整的 GraphQL 集成方案，或者根据自己的需求进行修改和扩展。
