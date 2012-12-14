//
//  ViewController.m
//  testweb
//
//  Created by apple on 12-2-27.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "ViewController.h"

@implementation ViewController
@synthesize _webview;

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
//加载网络地址
//    NSString *path = @"http://223.4.82.92/public/html5/index.html";
//    NSURL *url = [[NSURL alloc] initWithString:path];
//    [_webview loadRequest:[NSURLRequest requestWithURL:url]];
    
//本地地址
//	NSString *  infoFilePath;
//    NSURLRequest *  request;
//    infoFilePath = [[NSBundle mainBundle] pathForResource:@"index" ofType:@"htm"];
//    request = [NSURLRequest requestWithURL:[NSURL fileURLWithPath:infoFilePath]];
//    [_webview loadRequest:request];
    
    _webview.scalesPageToFit = YES;
    NSString *resourcePath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"html5"];
    NSString *filePath  = [resourcePath stringByAppendingPathComponent:@"index.html"];
    NSStringEncoding enc = CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingUTF8);
    NSString *htmlstring =[[[NSString alloc] initWithContentsOfFile:filePath encoding:enc error:nil] autorelease]; 
    [_webview loadHTMLString:htmlstring baseURL:[NSURL fileURLWithPath:resourcePath]];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    //return YES;
    return( interfaceOrientation == UIInterfaceOrientationLandscapeLeft || interfaceOrientation == UIInterfaceOrientationLandscapeRight);
}

@end
