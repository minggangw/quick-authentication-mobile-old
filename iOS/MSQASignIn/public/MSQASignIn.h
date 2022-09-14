//------------------------------------------------------------------------------
//
// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
//------------------------------------------------------------------------------

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class MSQAAccountData;
@class MSQAConfiguration;

NS_ASSUME_NONNULL_BEGIN

/// Represents a completion block that takes a  `MSQAAccountData` on success or
/// an error if the operation fails.
typedef void (^MSQACompletionBlock)(MSQAAccountData *_Nullable account,
                                    NSError *_Nullable error);

/// This class signs the user in with Microsoft MSA account.
@interface MSQASignIn : NSObject

/// A shared `MSQASignIn` instance.
@property(class, nonatomic, readonly) MSQASignIn *sharedInstance;

/// Unavailable, use `sharedInstance` property to instantiate `MSQASignIn`.
/// :nodoc:
+ (instancetype)new NS_UNAVAILABLE;

/// Unavailable, use `sharedInstance` property to instantiate `MSQASignIn`.
/// :nodoc
- (instancetype)init NS_UNAVAILABLE;

/// This method should be called from your `UIApplicationDelegate`'s
/// `application:openURL:options` method.
///
/// @param url The URL passed to the app.
/// @param sourceApplication String that was gotten by
/// `UIApplicationOpenURLOptionsSourceApplicationKey`.
/// @return `YES` if `MSQASignIn` handled this URL.
- (BOOL)handleURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication;

/// Starts to acquire token interactively using the provided parameters.
///
/// @param scopes Permissions you want to included in the access token reveived
/// in the `completionBlock`.
/// @param controller The view controller used to present the authorization UI
/// from.
/// @param completionBlock The `MSQACompletionBlock` block that is called
/// on completion, and the block will be called asynchronously on the main
/// queue.
- (void)acquireTokenWithScopes:(NSArray<NSString *> *)scopes
                    controller:(UIViewController *)controller
               completionBlock:(MSQACompletionBlock)completionBlock;

/// Starts to acquire token silently using the provide parameters.
/// @param scopes  Permissions you want to included in the access token reveived
/// in the `completionBlock`.
/// @param completionBlock The `MSQACompletionBlock` block that is called
/// on completion, and the block will be called asynchronously on the main
/// queue.
- (void)acquireTokenSilentWithScopes:(NSArray<NSString *> *)scopes
                     completionBlock:(MSQACompletionBlock)completionBlock;

/// Starts to get the current account if there is an account that has already
/// signed in.
/// @param completionBlock The `MSQACompletionBlock` block that is called
/// on completion, and the block will be called asynchronously on the main
/// queue.
- (void)getCurrentAccountWithCompletionBlock:
    (MSQACompletionBlock)completionBlock;

/// Removes the token from the cache and mark the current user as being in
/// signed out state.
/// @param completionBlock The block that is called on completion.
- (void)signOutWithCompletionBlock:
    (void (^)(NSError *_Nullable error))completionBlock;

/// Configures the `MSQASignIn` by the provided  `MQAConfiguration` object.
/// @param configuration The configuration used for the `sharedInstance`
/// @param error The  error if the operation fails.
- (void)setConfiguration:(MSQAConfiguration *)configuration
                   error:(NSError *_Nullable *_Nullable)error;

// TODO(minggang): This declaration will be removed and use the one
// in MSQASignIn_Private.h.
- (void)signInWithViewController:(UIViewController *)controller
                 completionBlock:(MSQACompletionBlock)completionBlock;

@end

NS_ASSUME_NONNULL_END